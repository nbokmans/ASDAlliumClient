package nl.han.asd.project.client.commonclient.connection;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import com.google.inject.AbstractModule;

import nl.han.asd.project.commonservices.encryption.IEncryptionService;

/**
 * Bind the connection implementations to the
 * interfaces.
 *
 * @version 1.0
 */
public class ConnectionModule extends AbstractModule {

    public static class ConnectionServiceFactory implements IConnectionServiceFactory {

        IEncryptionService encryptionService;

        @Inject
        public ConnectionServiceFactory(IEncryptionService encryptionService) {
            this.encryptionService = encryptionService;
        }

        @Override
        public IConnectionService create(String host, int port, File publicKeyFile) throws IOException {
            return new ConnectionService(encryptionService, host, port, publicKeyFile);
        }

        @Override
        public IConnectionService create(String host, int port, byte[] publicKeyBytes) {
            return new ConnectionService(encryptionService, host, port, publicKeyBytes);
        }

        @Override
        public IConnectionService create(String host, int port) {
            return new ConnectionService(host, port);
        }
    }

    @Override
    protected void configure() {
        bind(IConnectionServiceFactory.class).to(ConnectionServiceFactory.class);
    }

}
