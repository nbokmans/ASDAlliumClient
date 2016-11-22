package integration.nl.han.asd.project.client.commonclient;

import com.xebialabs.overcast.host.DockerHost;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.message.IMessageReceiver;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.client.commonclient.store.NoConnectedNodesException;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class FullIntegrationIT extends ITHelper {

    private Message receivedMessage;
    private String confirmedMessage;
    private String username = "OnionTest";
    private String password = "test1234";
    private String textMessage = "TEST Message";
    private String otherUser = "user";

    @Before
    public void setup() {
        receivedMessage = null;
        confirmedMessage = null;
    }

    @After
    public void tearDown() throws SQLException {
        database.resetDatabase();

        if (master != null) {
            master.teardown();
        }
        if (!nodes.isEmpty()) {
            for (DockerHost node : nodes) {
                node.teardown();
            }
            nodes.clear();
        }
        if (client != null) {
            client.teardown();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testRegisterClientWithoutMasterReturnsError() throws IOException, MessageNotSentException {
        masterHost = "tumma.nl";
        masterPort = 9934;
        injectInterfaces();

        commonClientGateway.registerRequest(username, password, password);
    }

    @Test
    public void testRegisterClientReturnsSuccessMessage() throws IOException, MessageNotSentException {
        startMaster();
        injectInterfaces();

        Assert.assertEquals(HanRoutingProtocol.ClientRegisterResponse.Status.SUCCES,
                commonClientGateway.registerRequest(username, password, password));
    }

    @Test(expected = NoConnectedNodesException.class)
    public void testLogInExistingClientReturnsSuccessMessage() throws Exception {
        startMaster();
        injectInterfaces();

        commonClientGateway.registerRequest(username, password, password);
        Assert.assertEquals(HanRoutingProtocol.ClientLoginResponse.Status.SUCCES,
                commonClientGateway.loginRequest(username, password));
        Contact currentUser = contactStore.getCurrentUser().asContact();
        Assert.assertEquals(currentUser.getUsername(), username);
        Assert.assertNotNull(contactStore.getCurrentUser().getSecretHash());
        Assert.assertEquals(currentUser.getConnectedNodes().length, 0);

        Assert.fail();
    }

    @Test(expected = NullPointerException.class)
    public void testSendMessageToNonExistingContactReturnError() throws Exception {
        startMaster();
        injectInterfaces();

        commonClientGateway.registerRequest(username, password, password);
        commonClientGateway.loginRequest(username, password);
        Message message = new Message(contactStore.getCurrentUser().asContact(),
                contactStore.findContact("OtherUser"), new Date(), textMessage);
        commonClientGateway.sendMessage(message);

        Assert.fail();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSendMessageToExistingContactWithoutExistingNodesReturnError() throws Exception {
        startMaster();
        injectInterfaces();

        commonClientGateway.registerRequest(username, password, password);
        commonClientGateway.loginRequest(username, password);
        contactStore.addContact(otherUser);
        Message message = new Message(contactStore.getCurrentUser().asContact(),
                contactStore.findContact(otherUser), new Date(), textMessage);
        commonClientGateway.sendMessage(message);

        Assert.fail();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSendMessageToOfflineContactWithExistingNodesReturnError() throws Exception {
        startMaster();
        startNodes(4);
        injectInterfaces();

        commonClientGateway.registerRequest(username, password, password);
        commonClientGateway.loginRequest(username, password);
        contactStore.addContact(otherUser);
        Message message = new Message(contactStore.getCurrentUser().asContact(),
                contactStore.findContact(otherUser), new Date(), textMessage);
        commonClientGateway.sendMessage(message);

        Assert.fail();
    }

    @Test
    public void testSendMessageToOnlineContactMessageDeliveredConfirmationReceived() {
        startMaster();
        startNodes(4);
        injectInterfaces();
        startClient("default");
        observeMessages();

        try {
            commonClientGateway.registerRequest(username, password, password);
            commonClientGateway.loginRequest(username, password);
            heartbeatService.startHeartbeatFor(contactStore.getCurrentUser());
            contactStore.addContact(otherUser);
            Message message = new Message(contactStore.getCurrentUser().asContact(),
                    contactStore.findContact(otherUser), new Date(), textMessage);
            commonClientGateway.sendMessage(message);

            Thread.sleep(5000);

            List<Message> messages = new ArrayList<>();
            messages.add(message);
            Assert.assertEquals(messages, persistence.getAllMessages());

            Assert.assertTrue(confirmedMessage != null && !confirmedMessage.isEmpty());
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testSendMessageToContactComingOnlineLaterDeliveredAfterTimeoutReceiveConfirmation() {
        startMaster();
        startNodes(4);
        injectInterfaces();
        observeMessages();

        try {
            commonClientGateway.registerRequest(username, password, password);
            commonClientGateway.loginRequest(username, password);
            contactStore.addContact(otherUser);
            Message message = new Message(contactStore.getCurrentUser().asContact(),
                    contactStore.findContact(otherUser), new Date(), textMessage);
            commonClientGateway.sendMessage(message);

        } catch (Exception e) {
            if (!(e instanceof IndexOutOfBoundsException)) {
                Assert.fail();
            }
        }
        try {
            Thread.sleep(15000);
            startClient("default");
            Assert.assertNull(confirmedMessage);
            Thread.sleep(80000);
        } catch (InterruptedException e1) {
            Assert.fail();
        }
        Assert.assertTrue(confirmedMessage != null && !confirmedMessage.isEmpty());
    }

    @Test
    public void testOtherUserSendsMessageWhichIsReceived() {
        startMaster();
        startNodes(4);
        injectInterfaces();
        observeMessages();

        try {
        commonClientGateway.registerRequest(username, password, password);
        commonClientGateway.loginRequest(username, password);
        contactStore.addContact(otherUser);

        startClient("send");

        Assert.assertEquals(receivedMessage.getReceiver(), contactStore.getCurrentUser().asContact());
        Assert.assertEquals(receivedMessage.getSender(), contactStore.findContact(otherUser));
        Assert.assertEquals(receivedMessage.getText(), textMessage);
        } catch (Exception  e) {
            Assert.fail();
        }
    }

    @Test
    public void testOtherUserSendsMessageWhileUserIsOfflineIsReceivedWhenUserComesOnline() {
        startMaster();
        startNodes(4);
        injectInterfaces();
        observeMessages();

        try {
            startClient("send");
            Assert.assertNull(receivedMessage);

            commonClientGateway.registerRequest(username, password, password);
            commonClientGateway.loginRequest(username, password);
            contactStore.addContact(otherUser);
            Thread.sleep(20000);

            Assert.assertEquals(receivedMessage.getReceiver(), contactStore.getCurrentUser().asContact());
            Assert.assertEquals(receivedMessage.getSender(), contactStore.findContact(otherUser));
            Assert.assertEquals(receivedMessage.getText(), textMessage);
        } catch (Exception  e) {
            Assert.fail();
        }
    }

    @Test
    public void testLogoutAndLoginAgainContactsAndMessagesRemain() {
        startMaster();
        startNodes(4);
        injectInterfaces();
        observeMessages();

        try {
            commonClientGateway.registerRequest(username, password, password);
            commonClientGateway.loginRequest(username, password);
            contactStore.addContact(otherUser);

            startClient("send");

            Assert.assertEquals(receivedMessage.getReceiver(), contactStore.getCurrentUser().asContact());
            Assert.assertEquals(receivedMessage.getSender(), contactStore.findContact(otherUser));
            Assert.assertEquals(receivedMessage.getText(), textMessage);

            commonClientGateway.logout();

            List<Message> messages = new ArrayList<>();
            Map<String, Contact> contacts = new HashMap<>();
            Assert.assertEquals(messages, persistence.getAllMessages());
            Assert.assertEquals(contacts, persistence.getContacts());

            commonClientGateway.loginRequest(username, password);

            messages.add(receivedMessage);
            contacts.put(otherUser, contactStore.findContact(otherUser));
            Assert.assertEquals(messages, persistence.getAllMessages());
            Assert.assertEquals(contacts, persistence.getContacts());
        } catch (Exception  e) {
            Assert.fail();
        }
    }

    private void observeMessages(){
        commonClientGateway.subscribeReceivedMessages(new IMessageReceiver() {
            @Override
            public void receivedMessage(Message message) {
                receivedMessage = message;
            }

            @Override
            public void confirmedMessage(String messageId) {
                confirmedMessage = messageId;
            }
        });
    }
}
