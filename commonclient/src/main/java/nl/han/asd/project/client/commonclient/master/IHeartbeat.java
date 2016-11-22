package nl.han.asd.project.client.commonclient.master;

import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientHeartbeat;

import java.io.IOException;

/**
 * Interface defining the heartbeat functions.
 *
 * @version 1.0
 */
public interface IHeartbeat {

    /**
     * Send the heartbeat to the master application.
     *
     * @param heartbeat the heartbeat to send to the server
     * @throws IOException              if an {@link IOException} occurred
     *                                  while preparing to send/sending the heartbeat
     * @throws IllegalArgumentException if heartbeat is null
     * @throws IOException              if the function was unable to send
     *                                  the wrapper due to a socket related
     *                                  exception
     * @throws MessageNotSentException  if the connection service
     *                                  was unable to send the message. Note that
     *                                  this exception is not thrown on Socket related
     *                                  exceptions. See IOException.
     */
    void sendHeartbeat(ClientHeartbeat heartbeat) throws IOException, MessageNotSentException;
}
