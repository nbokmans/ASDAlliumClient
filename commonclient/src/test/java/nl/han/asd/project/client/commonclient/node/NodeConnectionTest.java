package nl.han.asd.project.client.commonclient.node;

import nl.han.asd.project.client.commonclient.connection.ConnectionService;
import nl.han.asd.project.client.commonclient.connection.IConnectionService;
import nl.han.asd.project.client.commonclient.message.IReceiveMessage;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NodeConnectionTest {

    private NodeConnection nodeConnection;

    @Mock
    private IConnectionService connectionServiceMock;

    @Mock
    private IReceiveMessage receiveMessageMock;

    @Before
    public void setUp(){
        nodeConnection = new NodeConnection(connectionServiceMock, receiveMessageMock);
    }

    @Test
    public void stopNodeConnectionServiceStopConnectionServiceTest() {
        nodeConnection.stop();
        verify(connectionServiceMock).close();
    }


}
