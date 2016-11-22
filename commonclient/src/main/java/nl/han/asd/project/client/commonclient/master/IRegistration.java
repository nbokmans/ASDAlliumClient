package nl.han.asd.project.client.commonclient.master;

import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientRegisterRequest;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientRegisterResponse;

import java.io.IOException;

/**
 * Interface defining the registration methods.
 *
 * @version 1.0
 */
public interface IRegistration {

    /**
     * Send the register request to the server returning the
     * received response.
     *
     * @param request the request to be send to the master application
     * @return the response received from the server
     * @throws IllegalArgumentException if request is null
     * @throws IOException              if the function was unable to send
     *                                  the wrapper due to a socket related
     *                                  exception
     * @throws MessageNotSentException  if the connection service
     *                                  was unable to send the message. Note that
     *                                  this exception is not thrown on Socket related
     *                                  exceptions. See IOException.
     */
    ClientRegisterResponse register(ClientRegisterRequest request) throws IOException, MessageNotSentException;
}
