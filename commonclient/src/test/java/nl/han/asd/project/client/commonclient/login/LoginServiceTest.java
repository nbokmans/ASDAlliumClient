package nl.han.asd.project.client.commonclient.login;

import com.google.protobuf.ByteString;
import nl.han.asd.project.client.commonclient.master.IAuthentication;
import nl.han.asd.project.client.commonclient.master.ILogout;
import nl.han.asd.project.client.commonclient.node.IConnectedNodes;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.client.commonclient.store.CurrentUser;
import nl.han.asd.project.client.commonclient.store.IContactStore;
import nl.han.asd.project.commonservices.encryption.IEncryptionService;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginRequest;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginResponse;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLogoutRequest;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLogoutResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Contact.class, LoginService.class })
public class LoginServiceTest {

    /* Valid credentials */
    private static final String VALID_USERNAME = "TestUsername";
    private static final String VALID_PASSWORD = "TestPassword";
    /* Invalid credentials */
    private static final String INVALID_USERNAME_EMPTY = "";
    private static final String INVALID_PASSWORD_EMPTY = "";
    private static final String INVALID_USERNAME_FORBIDDEN_CHARACTERS = "Test^User";
    private static final String INVALID_USERNAME_TOO_LONG = ""; // Aanname dat de maximum lengte van een username 12 tekens is.
    private static final String INVALID_PASSWORD_TOO_LONG = ""; // Aanname dat de maximum lengte van een password 16 tekens is.
    private static final String INVALID_USERNAME_TOO_SHORT = ""; // Aanname dat de minimum lengte van een username 3 tekens is.
    private static final String INVALID_PASSWORD_TOO_SHORT = ""; // Aanname dat de minimum lengte van een password 8 tekens is.
    private static final String INVALID_USERNAME_NULL = null;
    private static final String INVALID_PASSWORD_NULL = null;
    private static final String VALID_SECRET_HASH = "SecretHash";
    private static final String INVALID_SECRET_HASH_NULL = null;

    private IAuthentication authenticationMock;
    private ILogout logoutMock;
    private IEncryptionService encryptionServiceMock;
    private IConnectedNodes setConnectedNodes;
    private IContactStore contactStore;

    private ILoginService login;

    @Before
    public void setUp() {
        authenticationMock = mock(IAuthentication.class);
        logoutMock = mock(ILogout.class);
        encryptionServiceMock = mock(IEncryptionService.class);
        setConnectedNodes = mock(IConnectedNodes.class);
        contactStore = mock(IContactStore.class);
        login = new LoginService(authenticationMock, logoutMock, encryptionServiceMock, setConnectedNodes, contactStore);
    }

    @Test(expected = IllegalUsernameException.class)
    public void testLoginIsUsernameEmpty() throws Exception {
        login.login(INVALID_USERNAME_EMPTY, VALID_PASSWORD);
    }

    @Test(expected = IllegalPasswordException.class)
    public void testLoginIsPasswordEmpty() throws Exception {
        login.login(VALID_USERNAME, INVALID_PASSWORD_EMPTY);
    }

    @Test(expected = IllegalUsernameException.class)
    public void testLoginIsUsernameTooLong() throws Exception {
        login.login(INVALID_USERNAME_TOO_LONG, VALID_PASSWORD);
    }

    @Test(expected = IllegalPasswordException.class)
    public void testLoginIsPasswordTooLong() throws Exception {
        login.login(VALID_USERNAME, INVALID_PASSWORD_TOO_LONG);
    }

    @Test(expected = IllegalUsernameException.class)
    public void testLoginIsUsernameTooShort() throws Exception {
        login.login(INVALID_USERNAME_TOO_SHORT, VALID_PASSWORD);
    }

    @Test(expected = IllegalPasswordException.class)
    public void testLoginIsPasswordTooShort() throws Exception {
        login.login(VALID_USERNAME, INVALID_PASSWORD_TOO_SHORT);
    }

    @Test(expected = IllegalUsernameException.class)
    public void testLoginUsernameContainsForbiddenCharacters() throws Exception {
        login.login(INVALID_USERNAME_FORBIDDEN_CHARACTERS, VALID_PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginIsUsernameNull() throws Exception {
        login.login(INVALID_USERNAME_NULL, VALID_PASSWORD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginIsPasswordNull() throws Exception {
        login.login(VALID_USERNAME, INVALID_PASSWORD_NULL);
    }

    @Test(expected = IllegalUsernameException.class)
    public void testLogoutIsUsernameEmpty() throws Exception {
        login.logout(INVALID_USERNAME_EMPTY, VALID_SECRET_HASH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLogoutIsSecretHashNull() throws Exception {
        login.login(VALID_USERNAME, INVALID_SECRET_HASH_NULL);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void loginStatusFailed() throws Exception {
        when(encryptionServiceMock.getPublicKey()).thenReturn("key".getBytes());

        ClientLoginResponse.Builder responseBuilder = ClientLoginResponse.newBuilder();
        responseBuilder.setStatus(ClientLoginResponse.Status.FAILED);
        ClientLoginResponse response = responseBuilder.build();

        when(authenticationMock.login(any(ClientLoginRequest.class))).thenReturn(response);

        Assert.assertEquals(ClientLoginResponse.Status.FAILED, login.login(VALID_USERNAME, VALID_PASSWORD));
    }

    @Test
    public void loginStatusSuccess() throws Exception {
        when(encryptionServiceMock.getPublicKey()).thenReturn("key".getBytes());

        ClientLoginResponse.Builder responseBuilder = ClientLoginResponse.newBuilder();
        responseBuilder.setStatus(ClientLoginResponse.Status.SUCCES);
        responseBuilder.setSecretHash("hash");
        responseBuilder.addConnectedNodes("127.0.0.1:1331");
        responseBuilder.addConnectedNodes("127.0.0.1:1332");
        responseBuilder.addConnectedNodes("127.0.0.1:1333");
        ClientLoginResponse response = responseBuilder.build();

        when(authenticationMock.login(any(ClientLoginRequest.class))).thenReturn(response);

        CurrentUser contactMock = mock(CurrentUser.class);
        whenNew(CurrentUser.class).withAnyArguments().thenReturn(contactMock);

        Assert.assertEquals(ClientLoginResponse.Status.SUCCES, login.login(VALID_USERNAME, VALID_PASSWORD));

        verify(setConnectedNodes).setConnectedNodes(eq(response.getConnectedNodesList()), eq(VALID_USERNAME));
        verify(contactStore).setCurrentUser(eq(contactMock));
    }

    @Test(expected = MisMatchingException.class)
    public void logoutStatusFailed() throws Exception {
        ClientLogoutResponse.Builder responseBuilder = ClientLogoutResponse.newBuilder();
        responseBuilder.setStatus(ClientLogoutResponse.Status.FAILED);
        ClientLogoutResponse response = responseBuilder.build();

        when(logoutMock.logout(any(ClientLogoutRequest.class))).thenReturn(response);

        Assert.assertEquals(login.logout(VALID_USERNAME, VALID_SECRET_HASH), ClientLogoutResponse.Status.FAILED);
    }

    @Test
    public void logoutStatusSuccess() throws Exception {
        ClientLogoutResponse.Builder responseBuilder = ClientLogoutResponse.newBuilder();
        responseBuilder.setStatus(ClientLogoutResponse.Status.SUCCES);
        ClientLogoutResponse response = responseBuilder.build();

        when(logoutMock.logout(any(ClientLogoutRequest.class))).thenReturn(response);

        Assert.assertEquals(ClientLogoutResponse.Status.SUCCES, login.logout(VALID_USERNAME, VALID_SECRET_HASH));

        verify(setConnectedNodes).unsetConnectedNodes();
    }
}
