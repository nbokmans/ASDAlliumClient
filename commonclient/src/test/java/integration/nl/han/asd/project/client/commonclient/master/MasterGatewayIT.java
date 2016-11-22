package integration.nl.han.asd.project.client.commonclient.master;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.protobuf.ByteString;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;

import nl.han.asd.project.client.commonclient.connection.ConnectionModule;
import nl.han.asd.project.client.commonclient.master.IAuthentication;
import nl.han.asd.project.client.commonclient.master.MasterGateway;
import nl.han.asd.project.client.commonclient.master.MasterModule;
import nl.han.asd.project.commonservices.encryption.EncryptionModule;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginRequest;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginResponse;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientRegisterRequest;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientRegisterResponse;

public class MasterGatewayIT {

    private static final String VALID_USERNAME = "valid_username";
    private static final String VALID_PASSWORD = "valid_password";

    private CloudHost master;
    private MasterGateway gateway;

    @Before
    public void setup() {
        master = CloudHostFactory.getCloudHost("master");
        master.setup();
        while (true) {
            try {
                new Socket(master.getHostName(), master.getPort(1337)).close();
                break;
            } catch (IOException e) {
                System.out.println("Trying again in two seconds");
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final Properties properties = new Properties();
        properties.setProperty("master-server-host", master.getHostName());
        properties.setProperty("master-server-port", Integer.toString(master.getPort(1337)));

        Injector injector = Guice.createInjector(new MasterModule(), new ConnectionModule(), new EncryptionModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Properties.class).toInstance(properties);
                    }
                });
        gateway = (MasterGateway) injector.getInstance(IAuthentication.class);
    }

    @After
    public void after() {
        master.teardown();
    }

    /* Registration of clients on master server */
    @Test
    public void testRegisterClientSuccessful() throws Exception {
        ClientRegisterRequest.Builder requestBuilder = ClientRegisterRequest.newBuilder();

        requestBuilder.setUsername(VALID_USERNAME);
        requestBuilder.setPassword(VALID_PASSWORD);

        ClientRegisterResponse response = gateway.register(requestBuilder.build());

        assertEquals(ClientRegisterResponse.Status.SUCCES, response.getStatus());
    }

    @Test
    public void testRegisterClientSameUsernameFails() throws Exception {
        ClientRegisterRequest.Builder requestBuilder = ClientRegisterRequest.newBuilder();
        requestBuilder.setUsername(VALID_USERNAME);
        requestBuilder.setPassword(VALID_PASSWORD);

        ClientRegisterResponse response = gateway.register(requestBuilder.build());
        assertEquals(ClientRegisterResponse.Status.SUCCES, response.getStatus());

        response = gateway.register(requestBuilder.build());
        assertEquals(ClientRegisterResponse.Status.TAKEN_USERNAME, response.getStatus());
    }

    /* Login of clients on master server */
    @Test
    public void testLoginSuccessful() throws Exception {
        ClientRegisterRequest.Builder reqisterRequestBuilder = ClientRegisterRequest.newBuilder();
        reqisterRequestBuilder.setUsername(VALID_USERNAME);
        reqisterRequestBuilder.setPassword(VALID_PASSWORD);

        ClientRegisterResponse registerResponse = gateway.register(reqisterRequestBuilder.build());
        assertEquals(ClientRegisterResponse.Status.SUCCES, registerResponse.getStatus());

        ClientLoginRequest.Builder loginRequestBuilder = ClientLoginRequest.newBuilder();
        loginRequestBuilder.setUsername(VALID_USERNAME);
        loginRequestBuilder.setPassword(VALID_PASSWORD);
        loginRequestBuilder.setPublicKey(ByteString.EMPTY);

        ClientLoginResponse loginResponse = gateway.login(loginRequestBuilder.build());
        assertEquals(ClientLoginResponse.Status.SUCCES, loginResponse.getStatus());
    }

    /* Get updated graph from master server */
    // TODO: Tests for when we actually add real nodes & see if the right node is added to master.
    @Test
    public void testGetUpdatedGraphSuccessful() {
        // Assert.assertTrue(true);
        /* TODO: gateway.getGraphUpdates(0).getLast().newVersion >= gateway
                .getCurrentGraphVersion()*/
    }

    /* Get active client group from master server */

    @Test
    @Ignore("Has to be fixed.") //TODO: Fix test?
    public void testGetClientGroupSuccessful() {
        //        ClientGroupResponseWrapper response = gateway.getClientGroup();
        //        Assert.assertTrue(response.clientGroup.size() >= 0);
    }
}
