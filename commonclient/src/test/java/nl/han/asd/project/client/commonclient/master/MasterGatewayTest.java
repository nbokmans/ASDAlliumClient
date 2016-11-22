package nl.han.asd.project.client.commonclient.master;

import com.google.protobuf.ByteString;
import nl.han.asd.project.client.commonclient.connection.IConnectionService;
import nl.han.asd.project.client.commonclient.connection.IConnectionServiceFactory;
import nl.han.asd.project.protocol.HanRoutingProtocol.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class MasterGatewayTest {

    private String host = "localhost";
    private int port = 1024;

    private Properties properties = new Properties();

    private IConnectionService connectionServiceMock;
    private IConnectionServiceFactory connectionServiceFactoryMock;

    private MasterGateway masterGateway;

    @Before
    public void setup() throws Exception {
        properties.setProperty("master-server-host", host);
        properties.setProperty("master-server-port", Integer.toString(port));

        connectionServiceMock = mock(IConnectionService.class);
        connectionServiceFactoryMock = mock(IConnectionServiceFactory.class);

        when(connectionServiceFactoryMock.create(eq(host), eq(port))).thenReturn(connectionServiceMock);
        masterGateway = new MasterGateway(properties, connectionServiceFactoryMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullProperties() throws Exception {
        new MasterGateway(null, connectionServiceFactoryMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullFactory() throws Exception {
        new MasterGateway(properties, null);
    }

    @Test
    public void constructorWithKey() throws Exception {
        String keyFileLocation = ".";

        properties.setProperty("master-server-keyfile", keyFileLocation);

        when(connectionServiceFactoryMock.create(eq(host), eq(port))).thenReturn(connectionServiceMock);
        new MasterGateway(properties, connectionServiceFactoryMock);
        verify(connectionServiceFactoryMock).create(eq(host), eq(port), eq(new File(keyFileLocation)));
    }

    @Test
    public void constructorValid() throws Exception {
        when(connectionServiceFactoryMock.create(eq(host), eq(port))).thenReturn(connectionServiceMock);
        new MasterGateway(properties, connectionServiceFactoryMock);

        // ignore the extra create from the junit setup
        verify(connectionServiceFactoryMock, times(2)).create(eq(host), eq(port));
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerNullRequest() throws Exception {
        masterGateway.register(null);
    }

    @Test
    public void registerValid() throws Exception {
        ClientRegisterRequest.Builder requestBuilder = ClientRegisterRequest.newBuilder();
        requestBuilder.setUsername("username");
        requestBuilder.setPassword("password");

        ClientRegisterRequest request = requestBuilder.build();

        Wrapper.Builder wrapperBuilder = Wrapper.newBuilder();
        wrapperBuilder.setData(request.toByteString());
        wrapperBuilder.setType(Wrapper.Type.CLIENTREGISTERREQUEST);

        Wrapper wrapper = wrapperBuilder.build();

        when(connectionServiceMock.wrap(eq(request), eq(Wrapper.Type.CLIENTREGISTERREQUEST))).thenReturn(wrapper);

        ClientRegisterResponse.Builder responseBuilder = ClientRegisterResponse.newBuilder();
        responseBuilder.setStatus(ClientRegisterResponse.Status.SUCCES);

        ClientRegisterResponse response = responseBuilder.build();

        when(connectionServiceMock.writeAndRead(eq(wrapper))).thenReturn(response);

        assertEquals(response, masterGateway.register(request));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendHeartbeatNullHeartbeat() throws Exception {
        masterGateway.sendHeartbeat(null);
    }

    @Test
    public void sendHeartbeatValid() throws Exception {
        ClientHeartbeat.Builder heartbeatBuilder = ClientHeartbeat.newBuilder();
        heartbeatBuilder.setUsername("username");
        heartbeatBuilder.setSecretHash("hash");

        ClientHeartbeat heartbeat = heartbeatBuilder.build();

        Wrapper.Builder wrapperBuilder = Wrapper.newBuilder();
        wrapperBuilder.setData(heartbeat.toByteString());
        wrapperBuilder.setType(Wrapper.Type.CLIENTHEARTBEAT);

        Wrapper wrapper = wrapperBuilder.build();

        when(connectionServiceMock.wrap(eq(heartbeat), eq(Wrapper.Type.CLIENTHEARTBEAT))).thenReturn(wrapper);

        masterGateway.sendHeartbeat(heartbeat);

        verify(connectionServiceMock).write(eq(wrapper));
    }

    @Test(expected = IllegalArgumentException.class)
    public void loginNullRequest() throws Exception {
        masterGateway.login(null);
    }

    @Test
    public void loginValid() throws Exception {
        ClientLoginRequest.Builder requestBuilder = ClientLoginRequest.newBuilder();
        requestBuilder.setUsername("username");
        requestBuilder.setPassword("password");
        requestBuilder.setPublicKey(ByteString.copyFrom("public key".getBytes()));

        ClientLoginRequest request = requestBuilder.build();

        Wrapper.Builder wrapperBuilder = Wrapper.newBuilder();
        wrapperBuilder.setData(request.toByteString());
        wrapperBuilder.setType(Wrapper.Type.CLIENTLOGINREQUEST);

        Wrapper wrapper = wrapperBuilder.build();

        when(connectionServiceMock.wrap(eq(request), eq(Wrapper.Type.CLIENTLOGINREQUEST))).thenReturn(wrapper);

        ClientLoginResponse.Builder responseBuilder = ClientLoginResponse.newBuilder();
        responseBuilder.setStatus(ClientLoginResponse.Status.SUCCES);

        ClientLoginResponse response = responseBuilder.build();

        when(connectionServiceMock.writeAndRead(eq(wrapper))).thenReturn(response);

        assertEquals(response, masterGateway.login(request));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUpdatedGraphNullRequest() throws Exception {
        masterGateway.getGraphUpdates(null);
    }

    @Test
    public void getUpdatedGraphValid() throws Exception {
        GraphUpdateRequest.Builder requestBuilder = GraphUpdateRequest.newBuilder();
        requestBuilder.setCurrentVersion(0);

        GraphUpdateRequest request = requestBuilder.build();

        Wrapper.Builder wrapperBuilder = Wrapper.newBuilder();
        wrapperBuilder.setData(request.toByteString());
        wrapperBuilder.setType(Wrapper.Type.GRAPHUPDATEREQUEST);

        Wrapper wrapper = wrapperBuilder.build();

        when(connectionServiceMock.wrap(eq(request), eq(Wrapper.Type.GRAPHUPDATEREQUEST))).thenReturn(wrapper);

        GraphUpdateResponse.Builder responseBuilder = GraphUpdateResponse.newBuilder();
        GraphUpdateResponse response = responseBuilder.build();

        when(connectionServiceMock.writeAndRead(eq(wrapper))).thenReturn(response);

        assertEquals(response, masterGateway.getGraphUpdates(request));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getClientGroupNullRequest() throws Exception {
        masterGateway.getClientGroup(null);
    }

    @Test
    public void getClientGroupValid() throws Exception {
        ClientRequest.Builder requestBuilder = ClientRequest.newBuilder();
        requestBuilder.setClientGroup(0);

        ClientRequest request = requestBuilder.build();

        Wrapper.Builder wrapperBuilder = Wrapper.newBuilder();
        wrapperBuilder.setData(request.toByteString());
        wrapperBuilder.setType(Wrapper.Type.CLIENTREQUEST);

        Wrapper wrapper = wrapperBuilder.build();

        when(connectionServiceMock.wrap(eq(request), eq(Wrapper.Type.CLIENTREQUEST))).thenReturn(wrapper);

        Client.Builder responseBuilder = Client.newBuilder();
        responseBuilder.setUsername("username");
        responseBuilder.setPublicKey(ByteString.copyFrom("public key".getBytes()));
        responseBuilder.addAllConnectedNodes(new ArrayList<String>());

        Client client = responseBuilder.build();

        ClientResponse.Builder builder = ClientResponse.newBuilder();
        builder.addClients(client);

        ClientResponse response = builder.build();

        when(connectionServiceMock.writeAndRead(eq(wrapper))).thenReturn(response);

        assertEquals(response, masterGateway.getClientGroup(request));
    }
}
