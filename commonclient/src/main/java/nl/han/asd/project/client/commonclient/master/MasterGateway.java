package nl.han.asd.project.client.commonclient.master;

import com.google.protobuf.GeneratedMessage;
import nl.han.asd.project.client.commonclient.connection.IConnectionService;
import nl.han.asd.project.client.commonclient.connection.IConnectionServiceFactory;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.commonservices.internal.utility.Check;
import nl.han.asd.project.protocol.HanRoutingProtocol.*;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper.Type;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Base implementation class used during the communication
 * with the master server.
 * <p/>
 * <p/>
 * Note that this implementation limits the number of open
 * connections to the master application to 1 meaning that
 * calls may block even on requests that do not expect a response
 * if the application has an active connection with the master server
 * prior to calling the send-method.
 *
 * @version 1.0
 */
public class MasterGateway implements IRegistration, IHeartbeat, IAuthentication, IGetClientGroup, IGetGraphUpdates, ILogout {

    private IConnectionService connectionService;

    /**
     * Construct a new MasterGateway instance using the provided
     * parameters.
     * <p/>
     * <p/>
     * Note that this implementation relies on the properties
     * defined in the properties parameter to find the required
     * connection and authentication details on socket construction.
     * <p/>
     * <p/>
     * The following properties are used:
     * <pre>
     *  properties name         | definition                                            | example
     *  {@link PropertyValues#MASTER_SERVER_HOST}       | the ip or hostname to connect to                      | "10.0.0.1"
     *  {@link PropertyValues#MASTER_SERVER_PORT}       | the port to connect to                                | 3333
     *  {@link PropertyValues#MASTER_SERVER_KEYFILE}    | keyfile containing the master's public key            | "/usr/keyfile"
     * </pre>
     * All packets are send plain-text if {@link PropertyValues#MASTER_SERVER_KEYFILE} is undefined.
     *
     * @param properties               properties instance containing the
     *                                 connection details
     * @param connectionServiceFactory factory to construct
     *                                 the connection service
     * @throws IllegalArgumentException if properties or
     *                                  encryptionService is null
     * @throws IOException              if unable to construct a new
     *                                  {@link IConnectionService} using the
     *                                  {@link PropertyValues#MASTER_SERVER_KEYFILE}
     */
    @Inject
    public MasterGateway(Properties properties, IConnectionServiceFactory connectionServiceFactory) throws IOException {
        Check.notNull(properties, "properties");
        Check.notNull(connectionServiceFactory, "connectionServiceFactory");

        String host = PropertyValues.MASTER_SERVER_HOST.get(properties);
        int port = PropertyValues.MASTER_SERVER_PORT.getInteger(properties);

        String keyfile = PropertyValues.MASTER_SERVER_KEYFILE.get(properties);

        // TODO: Enable encryption

        if (keyfile == null) {
            connectionService = connectionServiceFactory.create(host, port);
        } else {
            connectionService = connectionServiceFactory.create(host, port, new File(keyfile));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientRegisterResponse register(ClientRegisterRequest request) throws IOException, MessageNotSentException {
        Check.notNull(request, "request");

        Wrapper wrapper = connectionService.wrap(request, Type.CLIENTREGISTERREQUEST);
        return (ClientRegisterResponse) connectionService.writeAndRead(wrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendHeartbeat(ClientHeartbeat heartbeat) throws IOException, MessageNotSentException {
        Check.notNull(heartbeat, "heartbeat");

        Wrapper wrapper = connectionService.wrap(heartbeat, Type.CLIENTHEARTBEAT);
        connectionService.write(wrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientLoginResponse login(ClientLoginRequest request) throws IOException, MessageNotSentException {
        Check.notNull(request, "request");

        Wrapper wrapper = connectionService.wrap(request, Type.CLIENTLOGINREQUEST);
        GeneratedMessage response = connectionService.writeAndRead(wrapper);

        return (ClientLoginResponse) response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphUpdateResponse getGraphUpdates(GraphUpdateRequest request) throws IOException, MessageNotSentException {
        Check.notNull(request, "request");

        Wrapper wrapper = connectionService.wrap(request, Type.GRAPHUPDATEREQUEST);
        GeneratedMessage response = connectionService.writeAndRead(wrapper);

        return (GraphUpdateResponse) response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientResponse getClientGroup(ClientRequest request) throws IOException, MessageNotSentException {
        Check.notNull(request, "request");

        Wrapper wrapper = connectionService.wrap(request, Type.CLIENTREQUEST);
        GeneratedMessage response = connectionService.writeAndRead(wrapper);

        return (ClientResponse) response;
    }

    @Override
    public ClientLogoutResponse logout(ClientLogoutRequest request) throws IOException, MessageNotSentException {
        Check.notNull(request, "request");

        Wrapper wrapper = connectionService.wrap(request, Type.CLIENTLOGOUTREQUEST);
        GeneratedMessage response = connectionService.writeAndRead(wrapper);

        return (ClientLogoutResponse) response;
    }

    private enum PropertyValues {
        MASTER_SERVER_HOST("master-server-host", false),
        MASTER_SERVER_PORT("master-server-port", false),
        MASTER_SERVER_KEYFILE("master-server-keyfile", true);

        private String value;
        private boolean nullable;

        PropertyValues(String value, boolean nullable) {
            this.value = Check.notNull(value, "value");
            this.nullable = nullable;
        }

        String get(Properties properties) {
            String property = properties.getProperty(value);
            if (nullable) {
                return property;
            } else {
                return Check.notNull(property, value);
            }
        }

        Integer getInteger(Properties properties) {
            return Integer.valueOf(get(properties));
        }
    }

}
