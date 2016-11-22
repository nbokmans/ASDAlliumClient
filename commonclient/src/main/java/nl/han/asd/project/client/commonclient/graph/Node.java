package nl.han.asd.project.client.commonclient.graph;

import nl.han.asd.project.commonservices.internal.utility.Check;

import java.util.LinkedList;
import java.util.List;

/**
 * @version 1.0
 */
public class Node {
    private List<Edge> edges;
    private String id;
    private String ipAddress;
    private int port;
    private byte[] publicKey;

    public Node(String id, String ipAddress, int port, byte[] publicKey) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
        this.publicKey = publicKey;
        edges = new LinkedList<>();
    }

    @Override
    public boolean equals(Object anotherObj) {
        if (anotherObj == null) {
            return false;
        }
        if (!(anotherObj instanceof Node)) {
            return false;
        }
        return ((Node) anotherObj).getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void addEdge(Node destination, float distance) {
        Check.notNull(destination, "destination");
        Check.notNull(distance, "distance");

        edges.add(new Edge(destination.getId(), distance));
    }

    /**
     * @param destinationNodeId Contains the Id from the edge's destination node.
     * @return The edge that has been found with the destination node id.
     */
    public Edge getEdge(String destinationNodeId) {
        Check.notNull(destinationNodeId, "destinationNodeId");

        for (Edge edge : edges) {
            if (edge.getDestinationNodeId().equals(destinationNodeId)) {
                return edge;
            }
        }
        return null;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public String getId() {
        return id;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
