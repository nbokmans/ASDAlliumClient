package nl.han.asd.project.client.commonclient.graph;

import java.util.Map;

/**
 * Provides a bridge between the graph and any class that wants to use the graph.
 */
public interface IGetVertices {
    Map<String, Node> getVertices();
}
