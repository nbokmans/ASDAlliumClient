package nl.han.asd.project.client.commonclient.path;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.han.asd.project.client.commonclient.graph.IGetVertices;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.path.matrix.PathMatrix;
import nl.han.asd.project.client.commonclient.path.matrix.PathMatrix.PathOption;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.client.commonclient.store.NoConnectedNodesException;
import nl.han.asd.project.commonservices.internal.utility.Check;

/**
 * IGetMessagePath implementation using the PathMatrix class
 * to generate and cache paths;
 *
 * @version 1.0
 */
public class PathDeterminationService implements IGetMessagePath {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathDeterminationService.class);

    private static final int MAX_HOPS = 10;
    public static final int MAX_PATH_TRIES = 100;

    private static final Random random = new Random();

    private IGetVertices getVertices;

    private Map<String, Node> vertices;
    private PathMatrix pathMatrix;

    @Inject
    public PathDeterminationService(IGetVertices getVertices) {
        this.getVertices = Check.notNull(getVertices, "getVertices");
    }

    @Override
    public List<Node> getPath(int minHops, Contact contactReceiver) {
        if (minHops > MAX_HOPS || minHops < 0) {
            throw new IllegalArgumentException(minHops + " must be between 0 and " + MAX_HOPS);
        }
        Check.notNull(contactReceiver, "contactReceiver");

        updateMatrix();

        try {
            for (int i = -1; i < MAX_PATH_TRIES; i++) {
                List<Node> path = getRandomPathTo(minHops, getRandomConnectedNodeOf(contactReceiver));

                if (!path.isEmpty()) {
                    return path;
                }
            }
        } catch (NoConnectedNodesException e) {
            LOGGER.debug(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    private List<Node> getRandomPathTo(int minHops, Node endNode) {
        List<PathOption> pathOptions = pathMatrix.getOptions(endNode.getId(), minHops);

        if (pathOptions.isEmpty()) {
            return Collections.emptyList();
        }

        return pathOptions.get(random.nextInt(pathOptions.size())).getPath(vertices);
    }

    private Node getRandomConnectedNodeOf(Contact contactReceiver) throws NoConnectedNodesException {
        Node[] contactReceiverNodes = contactReceiver.getConnectedNodes();
        return contactReceiverNodes[random.nextInt(contactReceiverNodes.length)];
    }

    private void updateMatrix() {
        Map<String, Node> newVertices = getVertices.getVertices();
        if (!newVertices.equals(vertices)) {
            vertices = newVertices;
            pathMatrix = new PathMatrix(vertices, MAX_HOPS);
        }
    }

}
