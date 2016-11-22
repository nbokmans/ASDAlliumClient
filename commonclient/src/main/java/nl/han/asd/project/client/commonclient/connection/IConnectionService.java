package nl.han.asd.project.client.commonclient.connection;

import com.google.protobuf.GeneratedMessage;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper.Type;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Define available read and write operations.
 *
 * @version 1.0
 */
public interface IConnectionService {

    /**
     * Wrap the provided message in a new Wrapper instance encrypting
     * the message provided the public key of the receiver was defined.
     *
     * @param <T>     the type of message
     * @param message to-be-wrapped message
     * @param type    type referring to this message
     * @return Wrapper containing the message and the provided type
     * @throws IllegalArgumentException if message and/or type is null
     */
    public <T extends GeneratedMessage> Wrapper wrap(T message, Type type);

    /**
     * Transmit the provided message to the host.
     * <p/>
     * <p/>
     * This method should only be used for time-critical messages
     * for which transmission delay is not acceptable.
     * <p/>
     * <p/>
     * Note that the delay before sending the message is at most
     * equal to timeout excluding the time required to setup a new
     * connection.
     *
     * @param wrapper to-be-send wrapper
     * @param timeout maximum timout to wait before giving up
     *                and creating a new socket
     * @param unit    the time unit of the timeout parameter
     * @throws IOException             if the function was unable to send
     *                                 the wrapper due to a socket related
     *                                 exception
     * @throws MessageNotSentException if the connection service
     *                                 was unable to send the message. Note that
     *                                 this exception is not thrown on Socket related
     *                                 exceptions. See IOException.
     */
    public void write(Wrapper wrapper, long timeout, TimeUnit unit) throws IOException, MessageNotSentException;

    /**
     * Transmit the provided message to the host.
     * <p/>
     * <p/>
     * Note that this method may block for a long time
     * depending on the number of other threads waiting
     * to send a message.
     *
     * @param wrapper to-be-send wrapper
     * @throws IOException             if the function was unable to send
     *                                 the wrapper due to a socket related
     *                                 exception
     * @throws MessageNotSentException if the connection service
     *                                 was unable to send the message. Note that
     *                                 this exception is not thrown on Socket related
     *                                 exceptions. See IOException.
     */
    public void write(Wrapper wrapper) throws IOException, MessageNotSentException;

    /**
     * Transmit the provided message to the host and wait for the
     * host to send a response.
     * <p/>
     * <p/>
     * This method should only be used for time-critical messages
     * for which transmission delays are unacceptable.
     * <p/>
     * <p/>
     * Note that the delay before sending the message is at most
     * equal to timeout excluding the time required to setup a new
     * connection.
     *
     * @param wrapper to-be-send wrapper
     * @param timeout maximum timeout to wait before giving up
     *                and creating a new socket
     * @param unit    the time unit of the timeout parameter
     * @return the received response
     * @throws IOException             if the function was unable to send
     *                                 the wrapper due to a socket related
     *                                 exception
     * @throws MessageNotSentException if the connection service
     *                                 was unable to send the message. Note that
     *                                 this exception is not thrown on Socket related
     *                                 exceptions. See IOException.
     */
    public GeneratedMessage writeAndRead(Wrapper wrapper, long timeout, TimeUnit unit)
            throws IOException, MessageNotSentException;

    /**
     * Transmit the provided message to the host and wait for the
     * host to send a response.
     * <p/>
     * <p/>
     * Note that this method may block for a long time
     * depending on the number of other threads waiting
     * to send a message.
     *
     * @param wrapper to-be-send wrapper
     * @return the received response
     * @throws IOException             if the function was unable to send
     *                                 the wrapper due to a socket related
     *                                 exception
     * @throws MessageNotSentException if the connection service
     *                                 was unable to send the message. Note that
     *                                 this exception is not thrown on Socket related
     *                                 exceptions. See IOException.
     */
    public GeneratedMessage writeAndRead(Wrapper wrapper) throws IOException, MessageNotSentException;

    /**
     * Read a single GeneratedMessage from a connection
     *
     * @return GeneratedMessage The received message
     * @throws IOException
     */
    public GeneratedMessage read() throws IOException;

    /**
     * Close the connection
     */
    public void close();
}
