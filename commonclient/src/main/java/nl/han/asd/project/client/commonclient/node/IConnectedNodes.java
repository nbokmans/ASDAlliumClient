package nl.han.asd.project.client.commonclient.node;

import java.util.List;

/**
 * Define connectedNodes related methods.
 *
 * @version 1.0
 */
public interface IConnectedNodes {
    /**
     * Set the connectedNodes after successful login.
     *
     * @param connectedNodesList the list of connectedNodes
     */
    void setConnectedNodes(List<String> connectedNodesList, String username);

    /**
     * Unset connectedNodes when after logout.
     */
    void unsetConnectedNodes();
}
