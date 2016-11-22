package nl.han.asd.project.client.commonclient.path.matrix;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import nl.han.asd.project.client.commonclient.graph.Graph;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.path.matrix.PathMatrix.PathOption;

@RunWith(Enclosed.class)
public class PathMatrixTest {

    @RunWith(Parameterized.class)
    public static class WithParameters extends TestCase {

        static Map<String, Node> vertices = buildGraph();
        static PathMatrix pathMatrix = new PathMatrix(vertices, 10);

        @Parameters
        public static Collection<Case> data() {
            return Arrays.asList(new Case(1, "B", new Node[][] { { get("E"), get("C"), get("B") },
                    { get("A"), get("F"), get("B") }, { get("G"), get("C"), get("B") } }));
        }

        public WithParameters(Case testCase) {
            super(testCase, vertices, pathMatrix);
            pathMatrix.toString();
        }

        public static Node get(String id) {
            return vertices.get(id);
        }

        public static Map<String, Node> buildGraph() {
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

    @Ignore
    public static class TestCase {

        public static class Case {
            int hops;
            String destination;

            Node[][] expectedPaths;

            public Case(int hops, String destination, Node[][] expectedPaths) {
                this.hops = hops;
                this.destination = destination;
                this.expectedPaths = expectedPaths;
            }
        }

        Case testCase;

        Map<String, Node> vertices;
        PathMatrix pathMatrix;

        public TestCase(Case testCase, Map<String, Node> vertices, PathMatrix pathMatrix) {
            this.testCase = testCase;
            this.vertices = vertices;
            this.pathMatrix = pathMatrix;
        }

        @Test
        public void testPath() throws Exception {
            List<PathOption> pathOptions = pathMatrix.getOptions(testCase.destination, testCase.hops);

            for (Node[] path : testCase.expectedPaths) {
                boolean contained = false;

                for (PathOption option : pathOptions) {
                    if (Arrays.equals(option.getPath(vertices).toArray(), path)) {
                        contained = true;
                        break;
                    }
                }

                assertTrue(contained);
            }

            for (PathOption option : pathOptions) {
                boolean contained = false;

                for (Node[] path : testCase.expectedPaths) {
                    if (Arrays.equals(option.getPath(vertices).toArray(), path)) {
                        contained = true;
                        break;
                    }
                }

                assertTrue(option.getPath(vertices).toString(), contained);
            }
        }
    }
}
