package nl.han.asd.project.client.commonclient.master;

import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientRequest;

import java.io.IOException;

/**
 * Define the client group methods.
 *
 * @version 1.0
 */
public interface IGetClientGroup {

    /**
     * Send the client group request to the
     * server returning the received response.
     *
     * @param request the request to be send to the master application
     * @return the response received from the server
     * @throws IllegalArgumentException if request is null
     * @throws IOException              if an {@link IOException} occurred
     *                                  while preparing to send/sending the request
     * @throws MessageNotSentException  if the connection service
     *                                  was unable to send the message. Note that
     *                                  this exception is not thrown on Socket related
     *                                  exceptions. See IOException.
     */
    HanRoutingProtocol.ClientResponse getClientGroup(ClientRequest request) throws IOException, MessageNotSentException;
}
