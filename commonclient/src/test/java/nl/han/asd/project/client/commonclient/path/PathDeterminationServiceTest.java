package nl.han.asd.project.client.commonclient.path;

import nl.han.asd.project.client.commonclient.graph.Graph;
import nl.han.asd.project.client.commonclient.graph.IGetVertices;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.client.commonclient.store.NoConnectedNodesException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PathDeterminationServiceTest {

    @Mock
    IGetVertices getVertices;

    @Mock
    Contact contactReceiver;

    PathDeterminationService pathDeterminationService;

    @Ignore
    public void testNoDuplicateStartNodes() throws Exception {
        Map<String, Node> graph = buildGraph();
        when(getVertices.getVertices()).thenReturn(graph);

        when(contactReceiver.getConnectedNodes())
                .thenReturn(new Node[] { graph.get("C") });

        pathDeterminationService = new PathDeterminationService(
                getVertices);

        List<Node> path = pathDeterminationService.getPath(0, contactReceiver);
        List<Node> path2 = pathDeterminationService.getPath(0, contactReceiver);

        Assert.assertNotEquals(path.get(0), path2.get(0));
    }

    @Test
    public void testNoPathFromConnectedNode() throws NoConnectedNodesException {
        Map<String, Node> graph = buildGraph();
        when(getVertices.getVertices()).thenReturn(graph);

        when(contactReceiver.getConnectedNodes())
                .thenReturn(new Node[] { graph.get("H") });

        pathDeterminationService = new PathDeterminationService(
                getVertices);

        List<Node> path = pathDeterminationService.getPath(0, contactReceiver);

        Assert.assertArrayEquals(new Node[0], path.toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIGetVerticesNull() {
        PathDeterminationService pds = new PathDeterminationService(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathContactReceiverNull()
            throws NoConnectedNodesException {
        pathDeterminationService = new PathDeterminationService(
                getVertices);
        pathDeterminationService.getPath(0, null);
    }

    @Test
    public void testNoConnectedNodes() throws NoConnectedNodesException {
        Map<String, Node> graph = buildGraph();
        when(getVertices.getVertices()).thenReturn(graph);

        when(contactReceiver.getConnectedNodes())
                .thenThrow(new NoConnectedNodesException(""));

        pathDeterminationService = new PathDeterminationService(
                getVertices);

        List<Node> path = pathDeterminationService.getPath(0, contactReceiver);

        Assert.assertEquals(Collections.emptyList(), path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReceiverHasNoConnectedNodes()
            throws NoConnectedNodesException {
        Map<String, Node> graph = buildGraph();
        when(getVertices.getVertices()).thenReturn(graph);

        when(contactReceiver.getConnectedNodes()).thenReturn(new Node[] {});

        pathDeterminationService = new PathDeterminationService(
                getVertices);

        pathDeterminationService.getPath(0, contactReceiver);

        Assert.fail("Test failed. The service should've throwed an exception.");
    }

    public Map<String, Node> buildGraph() {
        Graph graph = new Graph();

        Node nodeA = new Node("A", "127.0.0.1", 1001, new byte[] { 0x00 });
        Node nodeB = new Node("B", "127.0.0.1", 1002, new byte[] { 0x00 });
        Node nodeC = new Node("C", "127.0.0.1", 1003, new byte[] { 0x00 });
        Node nodeD = new Node("D", "127.0.0.1", 1004, new byte[] { 0x00 });
        Node nodeE = new Node("E", "127.0.0.1", 1005, new byte[] { 0x00 });
        Node nodeF = new Node("F", "127.0.0.1", 1006, new byte[] { 0x00 });
        Node nodeG = new Node("G", "127.0.0.1", 1006, new byte[] { 0x00 });
        Node nodeH = new Node("H", "127.0.0.1", 1007, new byte[] { 0x00 });

        nodeA.addEdge(nodeE, 8);
        nodeA.addEdge(nodeF, 4);
        nodeE.addEdge(nodeC, 3);
        nodeC.addEdge(nodeD, 1);
        nodeC.addEdge(nodeB, 10);
        nodeF.addEdge(nodeB, 2);
        nodeF.addEdge(nodeD, 3);
        nodeG.addEdge(nodeE, 1);
        nodeG.addEdge(nodeC, 1);

        graph.addNodeVertex(nodeA);
        graph.addNodeVertex(nodeB);
        graph.addNodeVertex(nodeC);
        graph.addNodeVertex(nodeD);
        graph.addNodeVertex(nodeE);
        graph.addNodeVertex(nodeF);
        graph.addNodeVertex(nodeG);
        graph.addNodeVertex(nodeH);

        return graph.getGraphMap();
    }
}
