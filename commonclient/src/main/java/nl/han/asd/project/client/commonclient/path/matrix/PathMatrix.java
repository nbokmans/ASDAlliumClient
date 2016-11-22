package nl.han.asd.project.client.commonclient.path.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.han.asd.project.client.commonclient.graph.Edge;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.commonservices.internal.utility.Check;

/**
 * Calculate all best paths within
 * a specified range of hops;
 *
 * @version 1.0
 */
public class PathMatrix {
    class Element {
        Element previous;

        String node;
        float cost;

        Element(Element previous, String node, float cost) {
            this.previous = previous;
            this.node = node;
            this.cost = cost;
        }
    }

    /**
     * Returned by the PathMatrix class
     * representing a possible path to take.
     *
     * @version 1.0
     */
    public class PathOption {
        String src;
        String dest;

        Element startingElement;

        PathOption(String src, String dest, Element startingElement) {
            this.src = src;
            this.dest = dest;

            this.startingElement = startingElement;
        }

        /**
         * Get the path represented
         * by this instance.
         *
         * @param vertices vertices contained in the
         *      graph used to construct the path-list
         *
         * @return the constructed path
         *
         * @throws IllegalArgumentException if
         *      vertices is null
         */
        public List<Node> getPath(Map<String, Node> vertices) {
            Check.notNull(vertices, "vertices");

            List<Node> hops = new LinkedList<>();
            hops.add(vertices.get(src));

            Element curr = startingElement;
            while (curr.previous != null) {
                hops.add(vertices.get(curr.node));
                curr = curr.previous;
            }

            hops.add(vertices.get(dest));
            return hops;
        }
    }

    private String[] nodes;
    private Map<String, Integer> nodePositions;
    private Element[][][] data;

    /**
     * Construct a new PathMatrix instance. Note that this
     * constructor also constructs the matrix.
     *
     * @param vertices vertices contained in the
     *          graph
     * @param depth the maximum depth (hops) to consider
     *          when constructing the matrix
     *
     *  @throws IllegalArgumentException if vertices is null
     *          and/or the value of depth is less than 0
     */
    public PathMatrix(Map<String, Node> vertices, int depth) {
        if (vertices.isEmpty()) {
            return;
        }
        Check.notNull(vertices, "vertices");

        if (depth < 0) {
            throw new IllegalArgumentException(depth + " < 0");
        }

        nodes = new String[vertices.size()];
        data = new Element[vertices.size()][vertices.size()][depth];

        initializeNodePositions(vertices);
        initializeMatrix(vertices);

        for (int currDepth = 1; currDepth < data[0][0].length; currDepth++) {
            internalCalculate(currDepth);
        }
    }

    private void initializeMatrix(Map<String, Node> vertices) {
        int i;
        i = 0;
        for (Entry<String, Node> pair : vertices.entrySet()) {
            nodes[i] = pair.getKey();

            for (Edge edge : pair.getValue().getEdges()) {
                set(pair.getValue().getId(), edge.getDestinationNodeId(), edge.getWeight());
            }
            i++;
        }
    }

    private void initializeNodePositions(Map<String, Node> vertices) {
        nodePositions = new HashMap<>();
        int i = 0;
        for (Entry<String, Node> pair : vertices.entrySet()) {
            nodePositions.put(pair.getKey(), i);
            i++;
        }
    }

    private void set(String source, String destination, float cost) {
        int row = nodePositions.get(source);
        int col = nodePositions.get(destination);

        if (row == col) {
            return;
        }

        data[row][col][0] = new Element(null, destination, cost);
    }

    private void internalCalculate(int current) {
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                if (row == col) {
                    continue;
                }

                calculateCol(current, row, col);
            }
        }
    }

    private void calculateCol(int current, int row, int col) {
        for (int colRow = 0; colRow < data.length; colRow++) {
            if (colRow == row || colRow == col) {
                continue;
            }

            if (data[colRow][col][current - 1] == null || data[row][colRow][0] == null) {
                continue;
            }

            float cost = data[row][colRow][0].cost + data[colRow][col][current - 1].cost;

            Element element = new Element(data[colRow][col][current - 1], nodes[colRow], cost);

            if (element.previous.previous != null && element.previous.previous.node.equals(element.node)) {
                continue;
            }

            if (data[row][col][current] == null || data[row][col][current].cost > cost) {
                data[row][col][current] = element;
            }
        }
    }

    /**
     * Get a list of all possible path with the
     * provided number of hops that lead to the
     * specified destination.
     *
     * @param dest the destination of the path
     * @param hops the number of hops
     *
     * @return all found paths
     *
     * @throws IllegalArgumentException if dest is null
     *          and/or the value of hops is less than 0
     */
    public List<PathOption> getOptions(String dest, int hops) {
        Check.notNull(dest, "dest");

        if (hops < 0) {
            throw new IllegalArgumentException(hops + " < 0");
        }

        List<PathOption> options = new ArrayList<>(data.length);

        int destIndex = nodePositions.get(dest);
        for (int row = 0; row < data.length; row++) {
            Element element = data[row][destIndex][hops];
            if (element != null) {
                options.add(new PathOption(nodes[row], dest, element));
            }
        }

        return options;
    }

}
