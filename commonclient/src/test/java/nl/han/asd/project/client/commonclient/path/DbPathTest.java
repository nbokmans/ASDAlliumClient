package nl.han.asd.project.client.commonclient.path;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DbPathTest {

    private final String MESSAGE_ID = "127.0.0.1:1001";

    @Spy private DbNode dbNode1;
    @Spy private DbNode dbNode2;
    @Spy private DbNode dbNode3;

    private List<DbNode> dbNodeList;
    private DbPath dbPath;

    @Before
    public void setUp() {
        when(dbNode1.getNodeId()).thenReturn("1");
        when(dbNode1.getSequenceNumber()).thenReturn(1);
        when(dbNode2.getNodeId()).thenReturn("2");
        when(dbNode2.getSequenceNumber()).thenReturn(2);
        when(dbNode3.getNodeId()).thenReturn("3");
        when(dbNode3.getSequenceNumber()).thenReturn(3);

        dbNodeList = Arrays.asList(new DbNode[] { dbNode1, dbNode3, dbNode2});
        dbPath = new DbPath(MESSAGE_ID);
    }

    @Test
    public void testAddAndGetNodes() {
        for (DbNode dbNode:
                dbNodeList) {
            dbPath.addNode(dbNode);
        }

        Assert.assertEquals(dbNodeList, dbPath.getNodesInPath());
    }

    @Test
    public void testSort() {
        for (DbNode dbNode:
                dbNodeList) {
            dbPath.addNode(dbNode);
        }

        dbPath.sort();

        Assert.assertEquals(Arrays.asList(new DbNode[] { dbNode1, dbNode2, dbNode3}), dbPath.getNodesInPath());
    }


}
