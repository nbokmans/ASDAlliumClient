package nl.han.asd.project.client.commonclient.graph;

public interface IUpdateGraph extends AutoCloseable {
    /**
     * Update the graph to a newer version if necessary.
     */
    public void updateGraph();
}
