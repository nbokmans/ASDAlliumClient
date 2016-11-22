package nl.han.asd.project.client.commonclient.node;

import com.google.protobuf.ByteString;
import nl.han.asd.project.client.commonclient.connection.ConnectionService;
import nl.han.asd.project.client.commonclient.message.IReceiveMessage;
import nl.han.asd.project.client.commonclient.message.MessageProcessingService;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NodeConnectionService.class, ConnectionService.class, NodeConnection.class })
public class NodeConnectionServiceTest {

    private IReceiveMessage receiveMessageMock;
    private ConnectionService connectionServiceMock1;
    private ConnectionService connectionServiceMock2;
    private NodeConnection nodeConnectionMock1;
    private NodeConnection nodeConnectionMock2;

    private NodeConnectionService nodeConnectionService;

    @Before
    public void setUp() {
        receiveMessageMock = mock(MessageProcessingService.class);
        connectionServiceMock1 = mock(ConnectionService.class);
        connectionServiceMock2 = mock(ConnectionService.class);
        nodeConnectionMock1 = mock(NodeConnection.class);
        nodeConnectionMock2 = mock(NodeConnection.class);

        nodeConnectionService = new NodeConnectionService(receiveMessageMock);
    }

    @Test
    public void testSetConnectedNodes() throws Exception {
        List<String> connectedNodes = Arrays.asList(new String[] { "127.0.0.1:1024", "127.0.0.1:1025" });

        PowerMockito.whenNew(ConnectionService.class).withArguments(eq("127.0.0.1"), eq(1024)).thenReturn(connectionServiceMock1);
        PowerMockito.whenNew(ConnectionService.class).withArguments(eq("127.0.0.1"), eq(1025)).thenReturn(connectionServiceMock2);

        PowerMockito.whenNew(NodeConnection.class).withArguments(eq(connectionServiceMock1), eq(receiveMessageMock)).thenReturn(nodeConnectionMock1);
        PowerMockito.whenNew(NodeConnection.class).withArguments(eq(connectionServiceMock2), eq(receiveMessageMock)).thenReturn(nodeConnectionMock2);

        nodeConnectionService.setConnectedNodes(connectedNodes, "Alice");

        verify(connectionServiceMock1, times(1)).write(any(HanRoutingProtocol.Wrapper.class));
        verify(connectionServiceMock2, times(1)).write(any(HanRoutingProtocol.Wrapper.class));
    }

    @Test
    public void testUnsetConnectedNodes() throws Exception {
        List<String> connectedNodes = Arrays.asList(new String[] { "127.0.0.1:1024", "127.0.0.1:1025" });

        PowerMockito.whenNew(ConnectionService.class).withArguments(eq("127.0.0.1"), eq(1024)).thenReturn(connectionServiceMock1);
        PowerMockito.whenNew(ConnectionService.class).withArguments(eq("127.0.0.1"), eq(1025)).thenReturn(connectionServiceMock2);

        PowerMockito.whenNew(NodeConnection.class).withArguments(eq(connectionServiceMock1), eq(receiveMessageMock)).thenReturn(nodeConnectionMock1);
        PowerMockito.whenNew(NodeConnection.class).withArguments(eq(connectionServiceMock2), eq(receiveMessageMock)).thenReturn(nodeConnectionMock2);

        nodeConnectionService.setConnectedNodes(connectedNodes, "Alice");
        nodeConnectionService.unsetConnectedNodes();
    }

    @Test
    public void testSendingData() throws Exception {
        HanRoutingProtocol.Wrapper wrapper = HanRoutingProtocol.Wrapper.newBuilder().setData(
                ByteString.copyFrom(new byte[] { 0x10 })).setType(HanRoutingProtocol.Wrapper.Type.MESSAGE).build();
        HanRoutingProtocol.MessageWrapper messageWrapper = HanRoutingProtocol.MessageWrapper.newBuilder().setData(wrapper.toByteString()).setIPaddress("127.0.0.1").setPort(1025).setUsername(("Alice")).build();

        PowerMockito.whenNew(ConnectionService.class).withArguments(eq("127.0.0.1"), eq(1025)).thenReturn(connectionServiceMock1);

        nodeConnectionService.sendData(messageWrapper);

        verify(connectionServiceMock1, times(1)).write(eq(wrapper));
    }
}
