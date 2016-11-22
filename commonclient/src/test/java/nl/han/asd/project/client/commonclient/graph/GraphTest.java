package nl.han.asd.project.client.commonclient.graph;

import java.util.NoSuchElementException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.protobuf.ByteString;

import nl.han.asd.project.protocol.HanRoutingProtocol;

/**
 * @author Julius
 * @version 1.0
 * @since 29/04/16
 */
@RunWith(PowerMockRunner.class)
public class GraphTest {
    private final String EDGEDESTINATIONNODE = "NODE_2";
    private final int EDGEDESTINATIONNODEWEIGHT = 12;
    private final int NODE1_PORT = 1337;
    private final String NODE1_IP = "192.168.2.1";
    private final String NODE1_ID = "NODE_1";

    private final byte[] NODE1_PUBLICKEY = Base64.encodeBase64("12345".getBytes());

    private Graph graph;
    private HanRoutingProtocol.Node node;
    private HanRoutingProtocol.Edge edge;
    private Edge edgeNode1;

    @Before
    public void setUp() throws Exception {
        graph = new Graph();
        edge = HanRoutingProtocol.Edge.newBuilder().setTargetNodeId(EDGEDESTINATIONNODE)
                .setWeight(EDGEDESTINATIONNODEWEIGHT).build();
        edgeNode1 = new Edge(EDGEDESTINATIONNODE, EDGEDESTINATIONNODEWEIGHT);
        node = HanRoutingProtocol.Node.newBuilder().setPort(NODE1_PORT).setIPaddress(NODE1_IP).setId(NODE1_ID)
                .setPublicKey(ByteString.copyFrom(NODE1_PUBLICKEY)).addEdge(edge).build();
    }

    @Test
    public void testResetGraph() throws Exception {
        HanRoutingProtocol.Node node2 = HanRoutingProtocol.Node.newBuilder().setPort(1337).setIPaddress("192.168.2.1")
                .setId("NODE_2").setPublicKey(ByteString.copyFrom("12345".getBytes())).build();
        graph.addNodeVertex(node);
        graph.addNodeVertex(node2);
        Assert.assertEquals(2, graph.getVertexMapSize());
        graph.resetGraph();
        Assert.assertEquals(0, graph.getVertexMapSize());
    }

    @Test
    public void testAddHanProtocolEdge() {
        HanRoutingProtocol.Node node1 = HanRoutingProtocol.Node.newBuilder().setPort(1024).setIPaddress("192.168.2.1")
                .setId("NODE_1").setPublicKey(ByteString.copyFrom("12345".getBytes())).build();

        HanRoutingProtocol.Edge edge = HanRoutingProtocol.Edge.newBuilder().setTargetNodeId("NODE_1").setWeight(10)
                .build();

        HanRoutingProtocol.Node node2 = HanRoutingProtocol.Node.newBuilder().setPort(1025).setIPaddress("192.168.2.2")
                .setId("NODE_2").setPublicKey(ByteString.copyFrom("12345".getBytes())).addEdge(edge).build();

        graph.addNodeVertex(node1);
        graph.addNodeVertex(node2);

        Node nodeTwoInternalRef = graph.getNodeVertex("NODE_2");
        Assert.assertEquals(0, nodeTwoInternalRef.getEdges().size());

        graph.addEdgesToVertex(node2);
        Assert.assertEquals(1, nodeTwoInternalRef.getEdges().size());

    }

    @Test
    public void testAddNodeVertex() throws Exception {
        graph.addNodeVertex(node);
        Assert.assertEquals(1, graph.getVertexMapSize());

    }

    @Test(expected = NoSuchElementException.class)
    public void testAddEdgesToVertexWhenNodeDoesNotExistInGraph() throws Exception {
        graph.resetGraph();
        graph.addEdgesToVertex(node);
    }

    @Test
    public void testRemoveNodeVertex() throws Exception {

        graph.addNodeVertex(node);
        Assert.assertEquals(1, graph.getVertexMapSize());

        graph.removeNodeVertex(node);
        Assert.assertEquals(0, graph.getVertexMapSize());

    }

    @Test(expected = NoSuchElementException.class)
    public void testExeptionWhenGetVertexMapSizeCantFindVertex() {

        graph.addNodeVertex(node);

        Node nodeVertex = graph.getNodeVertex("NODE_2");

    }
}
