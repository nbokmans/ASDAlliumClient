package nl.han.asd.project.client.commonclient.node;

import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.protocol.HanRoutingProtocol;

/**
 * Send a MessageWrapper to a Node.
 *
 * @version 1.0
 */
public interface ISendData {

    /**
     * Send a MessageWrapper to a Node.
     *
     * @param messageWrapper The wrapper to be sent
     *
     * @throws MessageNotSentException
     */
    void sendData(HanRoutingProtocol.MessageWrapper messageWrapper) throws MessageNotSentException;
}
