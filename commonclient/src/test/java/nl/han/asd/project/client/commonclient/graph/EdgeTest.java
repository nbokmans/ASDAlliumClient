package nl.han.asd.project.client.commonclient.graph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EdgeTest {

    private Edge edge1, edge2;

    @Before
    public void setUp() {
        edge1 = new Edge("1", 10);
        edge2 = new Edge("2", 10);
    }

    @Test
    public void testEqualsImplementation() {
        Assert.assertEquals(false, edge1.equals(edge2));
    }

    @Test
    public void testHashcodeImplementation() {
        Assert.assertEquals(false, edge1.hashCode() == edge2.hashCode());
    }
}
