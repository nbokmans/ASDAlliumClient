package nl.han.asd.project.client.commonclient.node;

import com.google.inject.Inject;
import nl.han.asd.project.client.commonclient.connection.ConnectionService;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.message.IReceiveMessage;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Connects to all given connected nodes.
 * Keeps a list of open connections in order to close all open connections on exit.
 *
 * @version 1.0
 */
public class NodeConnectionService implements IConnectedNodes, ISendData {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeConnectionService.class);

    private IReceiveMessage receiveMessage;
    private List<NodeConnection> openConnections = new ArrayList<>();

    /**
     * Constructor of NodeConnectionService
     *
     * @param receiveMessage the receiveMessage interface
     */
    @Inject
    public NodeConnectionService(IReceiveMessage receiveMessage) {
        this.receiveMessage = receiveMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConnectedNodes(List<String> connectedNodes, String username) {
        for (String connectNode : connectedNodes) {
            String[] parts = connectNode.split(":");
            String hostname = parts[0];
            int port = Integer.parseInt(parts[1]);

            final ConnectionService connectionService = new ConnectionService(hostname, port);
            connectionService.closeOnIdle = false;

            HanRoutingProtocol.ClientNodeConnection.Builder builder = HanRoutingProtocol.ClientNodeConnection.newBuilder();
            builder.setUsername(username);

            try {
                HanRoutingProtocol.Wrapper wrapper = connectionService.wrap(builder.build(), HanRoutingProtocol.Wrapper.Type.CLIENTNODECONNECTION);
                connectionService.write(wrapper);
            } catch (IOException | MessageNotSentException e) {
                LOGGER.error(e.getMessage(), e);
            }

            NodeConnection nodeConnection = new NodeConnection(connectionService, receiveMessage);
            openConnections.add(nodeConnection);
            nodeConnection.start();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsetConnectedNodes() {
        for (NodeConnection openConnection : openConnections) {
            openConnection.stop();
        }
        openConnections = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendData(HanRoutingProtocol.MessageWrapper messageWrapper) throws MessageNotSentException {
        String hostname = messageWrapper.getIPaddress();
        int port = messageWrapper.getPort();

        try {
            HanRoutingProtocol.Wrapper wrapper = HanRoutingProtocol.Wrapper.parseFrom(messageWrapper.getData());

            ConnectionService connectionService = new ConnectionService(hostname, port);
            connectionService.write(wrapper);
        } catch (MessageNotSentException | IOException e) {
            throw new MessageNotSentException(e);
        }
    }
}
