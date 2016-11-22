package nl.han.asd.project.client.commonclient.connection;

import java.io.File;
import java.io.IOException;

/**
 * Factory methods to instantiate the
 * {@link IConnectionService} implementation using
 * the provided parameters during construction.
 *
 * @version 1.0
 */
public interface IConnectionServiceFactory {

    /**
     * Create a new {@link IConnectionService} instance used
     * to handle the communication with the specified host.
     * <p/>
     * <p/>
     * Note that this method does not check the validity
     * of the provided hostname or port.
     *
     * @param host to-be-connected to host
     * @param port port to use during connection
     * @return a new {@link IConnectionService} instance
     * @throws IllegalArgumentException if host is null
     */
    IConnectionService create(String host, int port);

    /**
     * Create a new {@link IConnectionService} instance used
     * to handle the communication with the specified host.
     * <p/>
     * <p/>
     * Note that this method does not check the validity
     * of the provided hostname or port.
     *
     * @param host           to-be-conencted to host
     * @param port           port to use during connection
     * @param publicKeyBytes public key of the to-be-connected to host
     * @return a new {@link IConnectionService} instance
     * @throws IllegalArgumentException if host and/or publicKeyBytes is null
     */
    IConnectionService create(String host, int port, byte[] publicKeyBytes);

    /**
     * Create a new {@link IConnectionService} instance used
     * to handle the communication with the specified host.
     * <p/>
     * <p/>
     * Note that this method does not check the validity
     * of the provided hostname or port.
     *
     * @param host          to-be-conencted to host
     * @param port          port to use during connection
     * @param publicKeyFile byte file containing the hosts public key
     * @return a new {@link IConnectionService} instance
     * @throws IllegalArgumentException if host and/or publicKeyFile is null
     * @throws IOException              if an IOException occurs during the publicKeyFile read
     */
    IConnectionService create(String host, int port, File publicKeyFile) throws IOException;
}
