package nl.han.asd.project.client.commonclient.login;

import com.google.protobuf.ByteString;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.master.IAuthentication;
import nl.han.asd.project.client.commonclient.master.ILogout;
import nl.han.asd.project.client.commonclient.node.IConnectedNodes;
import nl.han.asd.project.client.commonclient.store.CurrentUser;
import nl.han.asd.project.client.commonclient.store.IContactStore;
import nl.han.asd.project.commonservices.encryption.IEncryptionService;
import nl.han.asd.project.commonservices.internal.utility.Check;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginRequest;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginResponse;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLogoutRequest;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLogoutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Provide the methods used to login.
 *
 * @version 1.0
 */
public class LoginService implements ILoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private IContactStore contactStore;
    private IAuthentication authentication;
    private ILogout logout;
    private IEncryptionService encryptionService;
    private IConnectedNodes connectedNodes;

    /**
     * Construct a new LoginService.
     *
     * @param authentication the authentication interface
     * @param encryptionService the encryptionservice holding
     *          the public key
     * @param connectedNodes the connectedNodes interface
     *
     * @throws IllegalArgumentException if authentication
     *          or encryptionService is null
     */
    @Inject
    public LoginService(IAuthentication authentication, ILogout logout, IEncryptionService encryptionService,
                        IConnectedNodes connectedNodes, IContactStore contactStore) {
        this.contactStore = Check.notNull(contactStore, "contactStore");
        this.authentication = Check.notNull(authentication, "authentication");
        this.logout = Check.notNull(logout, "logout");
        this.encryptionService = Check.notNull(encryptionService, "encryptionService");
        this.connectedNodes = Check.notNull(connectedNodes, "connectedNodes");
    }

    /** {@inheritDoc} */
    @Override
    public ClientLoginResponse.Status login(String username, String password)
        throws InvalidCredentialsException, IOException, MessageNotSentException, SQLException {
        UserCheck.checkUsername(username);
        UserCheck.checkPassword(password);

        ClientLoginRequest.Builder loginRequest = ClientLoginRequest.newBuilder();
        loginRequest.setUsername(username);
        loginRequest.setPassword(hashPassword(password));
        loginRequest.setPublicKey(ByteString.copyFrom(encryptionService.getPublicKey()));

        ClientLoginResponse loginResponse = authentication.login(loginRequest.build());

        if (loginResponse.getStatus() != ClientLoginResponse.Status.SUCCES) {
            throw new InvalidCredentialsException(loginResponse.getStatus().name());
        }
        contactStore.init(username, password);

        contactStore.setCurrentUser(new CurrentUser(username, encryptionService.getPublicKey(), loginResponse.getSecretHash()));
        connectedNodes.setConnectedNodes(loginResponse.getConnectedNodesList(), username);

        return loginResponse.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientLogoutResponse.Status logout(String username, String secretHash)
            throws IOException, MessageNotSentException, MisMatchingException {
        UserCheck.checkUsername(username);
        Check.notNull(secretHash, "secretHash");

        ClientLogoutRequest.Builder logoutRequest = ClientLogoutRequest.newBuilder();
        logoutRequest.setUsername(username);
        logoutRequest.setSecretHash(secretHash);

        ClientLogoutResponse logoutResponse = logout.logout(logoutRequest.build());

        if (logoutResponse.getStatus() != ClientLogoutResponse.Status.SUCCES) {
            throw new MisMatchingException(logoutResponse.getStatus().name());
        }

        connectedNodes.unsetConnectedNodes();

        return logoutResponse.getStatus();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            byte[] digest = messageDigest.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("Hashing algorithm not supported");
        }

        return null;
    }
}
