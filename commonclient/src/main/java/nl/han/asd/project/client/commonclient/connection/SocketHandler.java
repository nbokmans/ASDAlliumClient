package nl.han.asd.project.client.commonclient.connection;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import nl.han.asd.project.commonservices.encryption.IEncryptionService;
import nl.han.asd.project.commonservices.internal.utility.Check;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper;
import nl.han.asd.project.protocol.HanRoutingProtocol.Wrapper.Builder;

import java.io.IOException;
import java.net.Socket;

/**
 * Handle all socket interactions.
 *
 * @version 1.0
 */
public class SocketHandler implements AutoCloseable {

    private String host;
    private int port;

    private IEncryptionService encryptionService;

    private Socket socket;

    /**
     * Construct a new SocketHandler instance
     * using the supplied host and port as the connection data.
     * <p/>
     * <p/>
     * Note that this constructor does not check
     * the validity of the provided parameters.
     * And delays the creation of the socket
     * until the first write call.
     *
     * @param host the host to connect to
     * @param port the port to use
     * @throws IllegalArgumentException if host is null
     */
    public SocketHandler(String host, int port) {
        this.host = Check.notNull(host, "host");
        this.port = port;
    }

    /**
     * Create a new SocketHandler instance using
     * the encryptionService as the decryption class
     * for received messages.
     * <p/>
     * <p/>
     * This construct internally calls
     * SocketHandler(String host, int port).
     *
     * @param host the host to connect to
     * @param port the port to use
     * @throws IllegalArgumentException if host or
     *                                  encryptionService is null
     */
    public SocketHandler(String host, int port, IEncryptionService encryptionService) {
        this(host, port);

        this.encryptionService = encryptionService;
    }

    /**
     * Transmit the supplied wrapper instance over the
     * network.
     * <p/>
     * <p/>
     * Note that if this instance holds no active
     * socket connection a new one is created using
     * the during construction created connection details.
     *
     * @param wrapper the wrapper to send over the network
     * @throws IOException              on socket related exceptions
     * @throws IllegalArgumentException if wrapper is null
     */
    public void write(Wrapper wrapper) throws IOException {
        Check.notNull(wrapper, "wrapper");

        if (socket == null) {
            socket = new Socket(host, port);
        }

        wrapper.writeDelimitedTo(socket.getOutputStream());
    }

    /**
     * Transmit the supplied wrapper instance over the
     * network and wait to receive a response.
     * <p/>
     * <p/>
     * Note that if this instance holds no active
     * socket connection a new one is created using
     * the during construction created connection details.
     *
     * @param wrapper the wrapper to send over the network
     * @return the received response
     * @throws IOException              on socket related exceptions
     * @throws IllegalArgumentException if wrapper is null
     */
    public GeneratedMessage writeAndRead(Wrapper wrapper) throws IOException {
        write(wrapper);

        Wrapper responseWrapper = Wrapper.parseDelimitedFrom(socket.getInputStream());

        if (encryptionService == null) {
            return Parser.parseFrom(responseWrapper);
        } else {
            byte[] decryptedData = encryptionService.decryptData(responseWrapper.getData().toByteArray());

            Builder wrapperBuilder = Wrapper.newBuilder();
            wrapperBuilder.setType(responseWrapper.getType());
            wrapperBuilder.setData(ByteString.copyFrom(decryptedData));

            return Parser.parseFrom(wrapperBuilder.build());
        }
    }

    /**
     * Read a single GeneratedMessage from the socket
     *
     * @return GeneratedMessage The received message
     * @throws IOException
     */
    public GeneratedMessage read() throws IOException {
        if (socket == null) {
            socket = new Socket(host, port);
        }

        Wrapper responseWrapper = Wrapper.parseDelimitedFrom(socket.getInputStream());

        if (encryptionService == null) {
            return Parser.parseFrom(responseWrapper);
        } else {
            byte[] decryptedData = encryptionService.decryptData(responseWrapper.getData().toByteArray());

            Builder wrapperBuilder = Wrapper.newBuilder();
            wrapperBuilder.setType(responseWrapper.getType());
            wrapperBuilder.setData(ByteString.copyFrom(decryptedData));

            return Parser.parseFrom(wrapperBuilder.build());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        socket.close();
        socket = null;
    }

}
