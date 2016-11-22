package nl.han.asd.project.client.commonclient.node;

import com.google.protobuf.GeneratedMessage;
import nl.han.asd.project.client.commonclient.connection.IConnectionService;
import nl.han.asd.project.client.commonclient.message.IReceiveMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Allows keeping a connection open with a Node.
 * Sends any incoming data to the MessageProcessingService.
 *
 * @version 1.0
 */
public class NodeConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeConnection.class);

    private IConnectionService connectionService;
    private IReceiveMessage receiveMessage;
    private volatile boolean isRunning = false;

    public NodeConnection(IConnectionService connectionService, IReceiveMessage receiveMessage) {
        this.connectionService = connectionService;
        this.receiveMessage = receiveMessage;
    }

    /**
     * Start the connection with the node, keep it open on another thread.
     */
    public void start() {
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        GeneratedMessage readMessage = connectionService.read();
                        receiveMessage.processIncomingMessage(readMessage);
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }).start();
    }

    /**
     * Stop the connection with the node.
     */
    public void stop() {
        isRunning = false;
        connectionService.close();
    }
}
