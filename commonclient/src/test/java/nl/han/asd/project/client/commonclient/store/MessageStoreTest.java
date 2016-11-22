package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.path.DbNode;
import nl.han.asd.project.client.commonclient.path.DbPath;
import nl.han.asd.project.client.commonclient.persistence.IPersistence;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.MockitoAnnotations;
import java.sql.SQLException;
import java.util.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Contact.class, ContactStore.class, MessageStore.class})
public class MessageStoreTest {
    private final String USERNAME = "test";
    private final String PASSWORD = "test123";

    @Mock
    private Message messageMock;

    @Mock
    Message message;

    @Mock
    Message message2;

    private String username = "username";
    private String password = "password";

    @Mock
    private IPersistence iPersistenceMock;

    private IMessageStore messageStore;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        messageStore = new MessageStore(iPersistenceMock);
    }

    @After
    public void tearDown() throws Exception {
        messageStore.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitInvalidParameterUsername() throws SQLException {
        messageStore.init(null, PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitInvalidParameterPassword() throws SQLException {
        messageStore.init(USERNAME, null);
    }

    @Test
    public void testInitValidParameter() throws SQLException {
        messageStore.init(USERNAME, PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMessageInvalidMessage() {
        messageStore.addMessage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMessagesFromUserInvalidParamterUsername() {
        messageStore.getMessagesFromUser(null);
    }

    @Test
    public void testGetMessagesFromUserValid() throws Exception {
        messageStore.addMessage(messageMock);

        final Contact contactMock1 = mock(Contact.class);
        when(contactMock1.getUsername()).thenReturn("Alice");
        final Contact contactMock2 = mock(Contact.class);
        when(contactMock2.getUsername()).thenReturn("Bob");

        when(messageMock.getText()).thenReturn("Test");
        when(messageMock.getMessageId()).thenReturn("1");
        when(messageMock.getMessageTimestamp()).thenReturn(new Date(100000));
        when(messageMock.getReceiver()).thenReturn(contactMock2);
        when(messageMock.getSender()).thenReturn(contactMock1);

        final List<Message> listOfMessages =  new ArrayList<>();
        listOfMessages.add(messageMock);
        when(iPersistenceMock.getAllMessagesPerContact()).thenReturn(new HashMap<Contact, List<Message>>() {{
            put(contactMock1, listOfMessages);
            put(contactMock2, null);
        }});

        PowerMockito.whenNew(Contact.class).withArguments(contactMock1.getUsername()).thenReturn(contactMock1);

        List<Message> message = messageStore.getMessagesFromUser(contactMock1.getUsername());
        Assert.assertEquals(1, message.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMessagesAfterDateInvalidTime() {
        messageStore.getMessagesAfterDate(-1);
    }

    @Test
    public void  testGetMessagesAfterDateValid() throws Exception {
        messageStore.addMessage(messageMock);

        final Contact contactMock1 = mock(Contact.class);
        when(contactMock1.getUsername()).thenReturn("Alice");
        final Contact contactMock2 = mock(Contact.class);
        when(contactMock2.getUsername()).thenReturn("Bob");

        when(messageMock.getText()).thenReturn("Test");
        when(messageMock.getMessageId()).thenReturn("1");
        when(messageMock.getMessageTimestamp()).thenReturn(new Date(100000));
        when(messageMock.getReceiver()).thenReturn(contactMock1);
        when(messageMock.getSender()).thenReturn(contactMock2);

        final List<Message> listOfMessages =  new ArrayList<Message>();
        listOfMessages.add(messageMock);
        when(iPersistenceMock.getAllMessagesPerContact()).thenReturn(new HashMap<Contact, List<Message>>() {{
            put(contactMock1, listOfMessages);
        }});

        PowerMockito.whenNew(Contact.class).withArguments(contactMock2.getUsername()).thenReturn(contactMock2);

        Message[] message = messageStore.getMessagesAfterDate(messageMock.getMessageTimestamp().getTime() - 10);
        Assert.assertEquals(1, message.length);
    }

    @Test
    public void testValidPath() {
        Node node1, node2, node3;
        node1 = mock(Node.class);
        when(node1.getId()).thenReturn("1");
        node2 = mock(Node.class);
        when(node2.getId()).thenReturn("2");
        node3 = mock(Node.class);
        when(node3.getId()).thenReturn("3");

        when(messageMock.getMessageId()).thenReturn("11111111");

        List<Node> path = Arrays.asList(node1, node2, node3);
        messageStore.addMessageWithPath(messageMock, path);

        DbPath dbPathMock = mock(DbPath.class);
        when(iPersistenceMock.getDatabasePath(messageMock.getMessageId())).thenReturn(dbPathMock);

        DbNode dbNode1, dbNode2, dbNode3;
        dbNode1 = mock(DbNode.class);
        when(dbNode1.getNodeId()).thenReturn("1");
        when(dbNode1.getSequenceNumber()).thenReturn(1);
        dbNode2 = mock(DbNode.class);
        when(dbNode2.getNodeId()).thenReturn("2");
        when(dbNode1.getSequenceNumber()).thenReturn(2);
        dbNode3 = mock(DbNode.class);
        when(dbNode3.getNodeId()).thenReturn("3");
        when(dbNode1.getSequenceNumber()).thenReturn(3);

        when(dbPathMock.getNodesInPath()).thenReturn(Arrays.asList(dbNode1, dbNode2, dbNode3));

        List<String> receivedPath = messageStore.getPathByMessageId(messageMock.getMessageId());
        Assert.assertEquals(3, receivedPath.size());

        for(int i = 0; i < 2; i++)
        {
            if (path.get(i).getId() != receivedPath.get(i))
                Assert.fail("Invalid id");
        }
    }

    @Test
    public void testInitializeMessageStoreTest() throws SQLException {
        messageStore.init(username, password);
        verify(iPersistenceMock).init(username, password);
    }

    @Test
    public void testAddMessage(){
        messageStore.addMessage(message);
        verify(iPersistenceMock).saveMessage(message);
    }

    @Test
    @PrepareForTest({Contact.class, MessageStore.class})
    public void testGetMessagesFromUser() throws Exception {
        Contact contactMock = PowerMockito.mock(Contact.class);
        HashMap<Contact, List<Message>> messagesFromUser = createMessagesUsers(contactMock);

        when(iPersistenceMock.getAllMessagesPerContact()).thenReturn(messagesFromUser);
        PowerMockito.whenNew(Contact.class).withArguments(username).thenReturn(contactMock);

        Assert.assertEquals(messagesFromUser.get(contactMock), messageStore.getMessagesFromUser(username));
    }

    @Test
    public void closeMessageStore() throws Exception {
        messageStore.close();
        verify(iPersistenceMock).close();
    }

    private HashMap<Contact, List<Message>> createMessagesUsers(Contact contact){
        HashMap<Contact, List<Message>> messagesFromUser = new HashMap<>();
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message2);
        messagesFromUser.put(contact, messages);

        return messagesFromUser;
    }
}
