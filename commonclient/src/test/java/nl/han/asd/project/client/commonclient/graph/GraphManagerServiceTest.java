package nl.han.asd.project.client.commonclient.graph;

import com.google.protobuf.ByteString;
import nl.han.asd.project.client.commonclient.connection.Parser;
import nl.han.asd.project.client.commonclient.master.MasterGateway;
import nl.han.asd.project.protocol.HanRoutingProtocol.GraphUpdate;
import nl.han.asd.project.protocol.HanRoutingProtocol.GraphUpdateRequest;
import nl.han.asd.project.protocol.HanRoutingProtocol.GraphUpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GraphManagerService.class, GraphUpdateResponse.class, GraphUpdateRequest.class, GraphUpdate.class,
        ByteString.class, Parser.class})
public class GraphManagerServiceTest {

    @Mock
    GraphUpdateResponse updatedGraphResponseWrapper;

    @Mock
    MasterGateway masterGateway;

    @Test
    public void testProcessGraphCurrentVersion() throws Exception {
        GraphUpdateRequest request = mock(GraphUpdateRequest.class);
        GraphUpdateRequest.Builder requestBuilder = mock(GraphUpdateRequest.Builder.class);
        when(requestBuilder.build()).thenReturn(request);
        when(requestBuilder.setCurrentVersion(0)).thenReturn(requestBuilder);

        PowerMockito.mockStatic(GraphUpdateRequest.class);
        PowerMockito.when(GraphUpdateRequest.newBuilder()).thenReturn(requestBuilder);

        GraphUpdate graphUpdateMock = mock(GraphUpdate.class);

        ByteString byteString = ByteString.copyFrom("graph update bytes".getBytes());

        GraphUpdateResponse response = mock(GraphUpdateResponse.class);
        when(response.getGraphUpdatesCount()).thenReturn(1);
        when(response.getGraphUpdates(0)).thenReturn(byteString);

        PowerMockito.mockStatic(Parser.class);
        PowerMockito.when(Parser.parseFrom("graph update bytes".getBytes(), GraphUpdate.class))
                .thenReturn(graphUpdateMock);

        when(graphUpdateMock.getNewVersion()).thenReturn(0);

        when(masterGateway.getGraphUpdates(request)).thenReturn(response);

        new GraphManagerService(masterGateway).processGraphUpdates();
    }

    @Test
    public void testProcessGraphFullGraph() throws Exception {
        GraphUpdateRequest request = mock(GraphUpdateRequest.class);
        GraphUpdateRequest.Builder requestBuilder = mock(GraphUpdateRequest.Builder.class);
        when(requestBuilder.build()).thenReturn(request);
        when(requestBuilder.setCurrentVersion(0)).thenReturn(requestBuilder);

        PowerMockito.mockStatic(GraphUpdateRequest.class);
        PowerMockito.when(GraphUpdateRequest.newBuilder()).thenReturn(requestBuilder);

        GraphUpdate graphUpdateMock = mock(GraphUpdate.class);

        ByteString byteString = ByteString.copyFrom("graph update bytes".getBytes());

        GraphUpdateResponse response = mock(GraphUpdateResponse.class);
        when(response.getGraphUpdatesCount()).thenReturn(1);
        when(response.getGraphUpdates(0)).thenReturn(byteString);

        PowerMockito.mockStatic(Parser.class);
        PowerMockito.when(Parser.parseFrom("graph update bytes".getBytes(), GraphUpdate.class))
                .thenReturn(graphUpdateMock);

        when(graphUpdateMock.getNewVersion()).thenReturn(10);

        Graph graphMock = mock(Graph.class);
        PowerMockito.whenNew(Graph.class).withAnyArguments().thenReturn(graphMock);

        when(graphUpdateMock.getIsFullGraph()).thenReturn(true);

        when(masterGateway.getGraphUpdates(request)).thenReturn(response);

        new GraphManagerService(masterGateway).processGraphUpdates();

        verify(graphMock).resetGraph();
    }
}