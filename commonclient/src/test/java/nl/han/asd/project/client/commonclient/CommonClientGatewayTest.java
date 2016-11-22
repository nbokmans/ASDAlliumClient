package nl.han.asd.project.client.commonclient;

import com.amazonaws.services.cloudfront.model.InvalidArgumentException;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.graph.IUpdateGraph;
import nl.han.asd.project.client.commonclient.heartbeat.IHeartbeatService;
import nl.han.asd.project.client.commonclient.login.ILoginService;
import nl.han.asd.project.client.commonclient.login.InvalidCredentialsException;
import nl.han.asd.project.client.commonclient.login.MisMatchingException;
import nl.han.asd.project.client.commonclient.master.IRegistration;
import nl.han.asd.project.client.commonclient.message.*;
import nl.han.asd.project.client.commonclient.store.*;
import nl.han.asd.project.client.commonclient.utility.Validation;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Validation.class})
public class CommonClientGatewayTest {

    @Mock
    IContactStore contactStoreMock;

    @Mock
    IMessageStore messageStoreMock;

    @Mock
    IRegistration registrationMock;

    @Mock
    ILoginService loginServiceMock;

    @Mock
    IScriptStore scriptStoreMock;

    @Mock
    ISendMessage sendMessageMock;

    @Mock
    ISubscribeMessageReceiver subscribeMessageReceiverMock;

    @Mock
    IHeartbeatService heartbeatServiceMock;

    @Mock
    IMessageConfirmation messageConfirmationMock;

    @Mock
    IUpdateGraph updateGraphMock;

    private CommonClientGateway commonClientGateway;
    private String username = "UserName";
    private String password = "Password";
    private String secretHash = "SecretHashFromUser";
    private String scriptName = "ScriptName";
    private String scriptContent = "ScriptContent";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        commonClientGateway = new CommonClientGateway(contactStoreMock, messageStoreMock, registrationMock,
                loginServiceMock, scriptStoreMock, sendMessageMock,subscribeMessageReceiverMock, heartbeatServiceMock,
                messageConfirmationMock, updateGraphMock);
    }

    @Test
    @PrepareForTest({Validation.class, HanRoutingProtocol.ClientRegisterRequest.Builder.class,
            HanRoutingProtocol.ClientRegisterRequest.class, HanRoutingProtocol.ClientRegisterResponse.class})
    public void testRegisterUserReturnSucces() throws IOException, MessageNotSentException {
        PowerMockito.mockStatic(Validation.class);
        PowerMockito.mockStatic(HanRoutingProtocol.ClientRegisterRequest.class);
        PowerMockito.mockStatic(HanRoutingProtocol.ClientRegisterResponse.class);
        HanRoutingProtocol.ClientRegisterRequest.Builder builder =
                PowerMockito.mock(HanRoutingProtocol.ClientRegisterRequest.Builder.class);
        HanRoutingProtocol.ClientRegisterResponse response = Mockito.mock(HanRoutingProtocol.ClientRegisterResponse.class);

        PowerMockito.when(Validation.passwordsEqual(any(String.class), any(String.class))).thenReturn(true);
        PowerMockito.when(Validation.validateCredentials(any(String.class), any(String.class))).thenReturn(true);
        when(Validation.validateCredentials(any(String.class), any(String.class))).thenReturn(true);
        when(HanRoutingProtocol.ClientRegisterRequest.newBuilder()).thenReturn(builder);
        when(registrationMock.register(any(HanRoutingProtocol.ClientRegisterRequest.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(HanRoutingProtocol.ClientRegisterResponse.Status.SUCCES);

        Assert.assertEquals(HanRoutingProtocol.ClientRegisterResponse.Status.SUCCES,
                commonClientGateway.registerRequest(username, password, password));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUserWithWrongCredentialsReturnError() throws IOException, MessageNotSentException {
        PowerMockito.mockStatic(Validation.class);

        PowerMockito.when(Validation.passwordsEqual(any(String.class), any(String.class))).thenReturn(true);
        PowerMockito.when(Validation.validateCredentials(any(String.class), any(String.class))).thenThrow(new IllegalArgumentException());

        commonClientGateway.registerRequest(username, password, password);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterUserNonEqualsPasswordReturnError() throws IOException, MessageNotSentException {
        PowerMockito.mockStatic(Validation.class);

        PowerMockito.when(Validation.passwordsEqual(any(String.class), any(String.class))).thenThrow(new IllegalArgumentException());

        commonClientGateway.registerRequest(username, password, password);
    }

    @Test(expected = NullPointerException.class)
    @PrepareForTest({Validation.class, HanRoutingProtocol.ClientRegisterRequest.Builder.class,
            HanRoutingProtocol.ClientRegisterRequest.class, HanRoutingProtocol.ClientRegisterResponse.class})
    public void testUnexpectedExceptionWhenRegisteringUserReturnError() throws IOException, MessageNotSentException {
        PowerMockito.mockStatic(Validation.class);
        PowerMockito.mockStatic(HanRoutingProtocol.ClientRegisterRequest.class);
        PowerMockito.mockStatic(HanRoutingProtocol.ClientRegisterResponse.class);

        PowerMockito.when(Validation.passwordsEqual(any(String.class), any(String.class))).thenReturn(true);
        PowerMockito.when(Validation.validateCredentials(any(String.class), any(String.class))).thenReturn(true);
        HanRoutingProtocol.ClientRegisterRequest.Builder builder =
                PowerMockito.mock(HanRoutingProtocol.ClientRegisterRequest.Builder.class);

        when(Validation.validateCredentials(any(String.class), any(String.class))).thenReturn(true);
        when(HanRoutingProtocol.ClientRegisterRequest.newBuilder()).thenReturn(builder);
        when(registrationMock.register(any(HanRoutingProtocol.ClientRegisterRequest.class))).thenThrow(new NullPointerException());

        commonClientGateway.registerRequest(username, password, password);
    }

    @Test
    @PrepareForTest({HanRoutingProtocol.ClientLoginResponse.class})
    public void testLoginUserReturnSucces() throws SQLException {
        PowerMockito.mock(HanRoutingProtocol.ClientLoginResponse.Status.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);

        try {
            when(loginServiceMock.login(any(String.class), any(String.class))).
                    thenReturn(HanRoutingProtocol.ClientLoginResponse.Status.SUCCES);
            when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);

            Assert.assertEquals(HanRoutingProtocol.ClientLoginResponse.Status.SUCCES,
                    commonClientGateway.loginRequest(username, password));
        } catch (Exception e) {
            Assert.fail();
        }

        Mockito.verify(messageStoreMock).init(username, password);
        Mockito.verify(heartbeatServiceMock).startHeartbeatFor(currentUser);
    }

    @Test
    @PrepareForTest({HanRoutingProtocol.ClientLoginResponse.class})
    public void testLoginUserWrongCredentialsReturnError() throws Exception {
        PowerMockito.mock(HanRoutingProtocol.ClientLoginResponse.Status.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);
        InvalidCredentialsException exception = Mockito.mock(InvalidCredentialsException.class);

        try {
            when(loginServiceMock.login(any(String.class), any(String.class))).
                    thenThrow(exception);
            when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);

            commonClientGateway.loginRequest(username, password);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidCredentialsException);
        }

        Mockito.verify(heartbeatServiceMock, Mockito.never()).startHeartbeatFor(currentUser);
        Mockito.verify(contactStoreMock).close();
        Mockito.verify(messageStoreMock).close();
    }

    @Test
    @PrepareForTest({HanRoutingProtocol.ClientLoginResponse.class})
    public void testUnexpectedExceptionWhenLoggingInReturnError() throws Exception {
        PowerMockito.mock(HanRoutingProtocol.ClientLoginResponse.Status.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);

        try {
            when(loginServiceMock.login(any(String.class), any(String.class))).
                    thenThrow(new NullPointerException());
            when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);

            commonClientGateway.loginRequest(username, password);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
        }

        Mockito.verify(heartbeatServiceMock, Mockito.never()).startHeartbeatFor(currentUser);
        Mockito.verify(contactStoreMock).close();
        Mockito.verify(messageStoreMock).close();
    }

    @Test
    @PrepareForTest({HanRoutingProtocol.ClientLogoutResponse.class})
    public void testLogoutWrongCredentialsReturnError() throws Exception {
        PowerMockito.mock(HanRoutingProtocol.ClientLogoutResponse.Status.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);
        Contact currentContact = Mockito.mock(Contact.class);
        MisMatchingException exception = Mockito.mock(MisMatchingException.class);

        when(currentUser.getSecretHash()).thenReturn(secretHash);
        when(currentUser.asContact()).thenReturn(currentContact);
        when(currentContact.getUsername()).thenReturn(username);

        try {
            when(loginServiceMock.logout(any(String.class), any(String.class))).
                    thenThrow(exception);
            when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);

            commonClientGateway.logout();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof MisMatchingException);
        }

        Mockito.verify(heartbeatServiceMock, Mockito.never()).stopHeartbeatFor(currentUser);
        Mockito.verify(messageConfirmationMock, Mockito.never()).close();
        Mockito.verify(messageStoreMock, Mockito.never()).close();
        Mockito.verify(contactStoreMock, Mockito.never()).close();
        Mockito.verify(updateGraphMock, Mockito.never()).close();
    }

    @Test
    @PrepareForTest({HanRoutingProtocol.ClientLogoutResponse.class})
    public void testLogoutReturnSuccessMessage() throws Exception {
        PowerMockito.mock(HanRoutingProtocol.ClientLogoutResponse.Status.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);
        Contact currentContact = Mockito.mock(Contact.class);

        when(currentUser.getSecretHash()).thenReturn(secretHash);
        when(currentUser.asContact()).thenReturn(currentContact);
        when(currentContact.getUsername()).thenReturn(username);

        try {
            when(loginServiceMock.logout(any(String.class), any(String.class))).
                    thenReturn(HanRoutingProtocol.ClientLogoutResponse.Status.SUCCES);
            when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);

            commonClientGateway.logout();
        } catch (Exception e) {
            Assert.fail();
        }

        Mockito.verify(heartbeatServiceMock).stopHeartbeatFor(currentUser);
        Mockito.verify(messageConfirmationMock).close();
        Mockito.verify(messageStoreMock).close();
        Mockito.verify(contactStoreMock).close();
        Mockito.verify(updateGraphMock).close();
    }

    @PrepareForTest({HanRoutingProtocol.ClientLogoutResponse.class})
    public void testUnexpectedExceptionWhenLoggingOut() throws Exception {
        PowerMockito.mock(HanRoutingProtocol.ClientLogoutResponse.Status.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);

        when(currentUser.getSecretHash()).thenReturn(secretHash);
        when(currentUser.asContact().getUsername()).thenReturn(username);

        try {
            when(loginServiceMock.logout(any(String.class), any(String.class))).
                    thenThrow(new NullPointerException());
            when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);

            commonClientGateway.logout();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
        }

        Mockito.verify(heartbeatServiceMock, Mockito.never()).stopHeartbeatFor(currentUser);
        Mockito.verify(messageConfirmationMock, Mockito.never()).close();
        Mockito.verify(messageStoreMock, Mockito.never()).close();
        Mockito.verify(contactStoreMock, Mockito.never()).close();
        Mockito.verify(updateGraphMock, Mockito.never()).close();
    }

    @Test
    public void testGetMessagesFromUser(){
        List<Message> expectedMessages = new ArrayList<>();
        when(messageStoreMock.getMessagesFromUser(username)).thenReturn(expectedMessages);

        Assert.assertEquals(expectedMessages, commonClientGateway.getMessagesFromUser(username));
    }

    @Test
    public void testGetCurrentUser(){
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);
        when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);

        Assert.assertEquals(currentUser, commonClientGateway.getCurrentUser());
    }

    @Test
    public void testGetContacts(){
        List<Contact> expectedContacts = new ArrayList<>();
        when(contactStoreMock.getAllContacts()).thenReturn(expectedContacts);

        Assert.assertEquals(expectedContacts, commonClientGateway.getContacts());
    }

    @Test
    public void testFindContact(){
        Contact contact = Mockito.mock(Contact.class);
        when(contactStoreMock.findContact(username)).thenReturn(contact);

        Assert.assertEquals(contact, commonClientGateway.findContact(username));
    }

    @Test
    public void testSendMessage(){
        Message message = Mockito.mock(Message.class);
        Contact receiver = Mockito.mock(Contact.class);
        when(message.getReceiver()).thenReturn(receiver);

        commonClientGateway.sendMessage(message);

        Mockito.verify(sendMessageMock).sendMessage(message, receiver);
    }

    @Test
    public void testAddContact(){
        commonClientGateway.addContact(username);

        Mockito.verify(contactStoreMock).addContact(username);
    }

    @Test
    public void testRemoveContact(){
        commonClientGateway.removeContact(username);

        Mockito.verify(contactStoreMock).removeContact(username);
    }

    @Test
    public void testGetAllScriptContent(){
        List<String> expectedScriptNames = new ArrayList<>();
        when(scriptStoreMock.getAllScriptNames()).thenReturn(expectedScriptNames);

        Assert.assertEquals(expectedScriptNames, commonClientGateway.getAllScriptNames());
    }

    @Test
    public void testGetScriptContent(){
        when(scriptStoreMock.getScriptContent(scriptName)).thenReturn(scriptContent);

        Assert.assertEquals(commonClientGateway.getScriptContent(scriptName), scriptContent);
    }

    @Test
    public void testAddScript(){
        commonClientGateway.addScript(scriptName);

        Mockito.verify(scriptStoreMock).addScript(scriptName);
    }

    @Test
    public void testUpdateScript(){
        commonClientGateway.updateScript(scriptName, scriptContent);

        Mockito.verify(scriptStoreMock).updateScript(scriptName, scriptContent);
    }

    @Test
    public void testRemoveScript(){
        commonClientGateway.removeScript(scriptName);

        Mockito.verify(scriptStoreMock).removeScript(scriptName);
    }

    @Test
    public void testSubscribeReceivedMessages(){
        IMessageReceiver messageReceiverMock = Mockito.mock(IMessageReceiver.class);
        commonClientGateway.subscribeReceivedMessages(messageReceiverMock);

        Mockito.verify(subscribeMessageReceiverMock).subscribe(messageReceiverMock);
    }

    @Test
    @PrepareForTest({Date.class, CommonClientGateway.class})
    public void testGetReceivedMessagesAfterDate(){
        Message[] messagesMock = {};
        Date dateMock = PowerMockito.mock(Date.class);
        long time = 1000;

        when(dateMock.getTime()).thenReturn(time);
        when(messageStoreMock.getMessagesAfterDate(time)).thenReturn(messagesMock);

        Assert.assertArrayEquals(messagesMock, commonClientGateway.getReceivedMessageAfterDate(dateMock));
    }

    @Test
    public void stopServicesWhenNoCurrentUserServicesAreNotStopped() throws Exception {
        when(contactStoreMock.getCurrentUser()).thenReturn(null);
        Mockito.doThrow(new InvalidArgumentException("")).when(heartbeatServiceMock).stopHeartbeatFor(null);

        commonClientGateway.stop();

        Mockito.verify(messageConfirmationMock, Mockito.never()).close();
        Mockito.verify(messageStoreMock, Mockito.never()).close();
        Mockito.verify(contactStoreMock, Mockito.never()).close();
        Mockito.verify(updateGraphMock, Mockito.never()).close();
    }


    @Test
    @PrepareForTest({Message.class, Date.class, CommonClientGateway.class})
    public void sendMessageToKnownContactReturnTrue() throws Exception {
        String messageText = "Message";
        Contact contact = Mockito.mock(Contact.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);
        Contact currentContact = Mockito.mock(Contact.class);
        Date dateMock = PowerMockito.mock(Date.class);
        Message messageMock = PowerMockito.mock(Message.class);

        when(contactStoreMock.findContact(username)).thenReturn(contact);
        when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.asContact()).thenReturn(currentContact);
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(dateMock);
        PowerMockito.whenNew(Message.class).withAnyArguments().thenReturn(messageMock);

        Assert.assertTrue(commonClientGateway.sendMessage(username, messageText));

        Mockito.verify(sendMessageMock).sendMessage(messageMock, contact);
    }

    @Test
    public void sendMessageToNonExistingContactReturnFalse() throws Exception {
        String messageText = "Message";

        when(contactStoreMock.findContact(username)).thenReturn(null);

        Assert.assertFalse(commonClientGateway.sendMessage(username, messageText));

        Mockito.verify(sendMessageMock, Mockito.never()).sendMessage(any(Message.class), any(Contact.class));
    }

    @Test
    @PrepareForTest({Message.class, Date.class, CommonClientGateway.class})
    public void sendMessageGoesWrongReturnTrueBecauseItIsInRetryList() throws Exception {
        String messageText = "Message";
        Contact contact = Mockito.mock(Contact.class);
        CurrentUser currentUser = Mockito.mock(CurrentUser.class);
        Contact currentContact = Mockito.mock(Contact.class);
        Date dateMock = PowerMockito.mock(Date.class);
        Message messageMock = PowerMockito.mock(Message.class);

        when(contactStoreMock.findContact(username)).thenReturn(contact);
        when(contactStoreMock.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.asContact()).thenReturn(currentContact);
        PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(dateMock);
        PowerMockito.whenNew(Message.class).withAnyArguments().thenReturn(messageMock);

        Assert.assertTrue(commonClientGateway.sendMessage(username, messageText));

        Mockito.verify(sendMessageMock).sendMessage(messageMock, contact);
    }
}
