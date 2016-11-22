package nl.han.asd.project.client.commonclient.path;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({DbNode.class, ResultSet.class})
public class DbNodeTest {

    private DbNode dbNode1;
    private DbNode dbNode2;
    private DbNode dbNode3;

    @Before public void setUp() {
        dbNode1 = new DbNode("1", 1);
        dbNode2 = new DbNode("2", 2);
        dbNode3 = new DbNode("3", 3);
    }

    @Test public void testFromDatabaseMethod() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(eq(3))).thenReturn(dbNode1.getNodeId());
        when(resultSet.getInt(4)).thenReturn(dbNode1.getSequenceNumber());

        DbNode stDbNode = DbNode.fromDatabase(resultSet);

        Assert.assertEquals(dbNode1.getNodeId(), stDbNode.getNodeId());
        Assert.assertEquals(dbNode1.getSequenceNumber(), stDbNode.getSequenceNumber());
    }

    @Test(expected = NullPointerException.class)
    public void testCompareToMethod() {
        Assert.assertEquals(-1, dbNode1.compareTo(dbNode2));
        Assert.assertEquals(1, dbNode3.compareTo(dbNode2));
        Assert.assertEquals(0, dbNode2.compareTo(dbNode2));

        dbNode2.compareTo(null);
    }

    @Test
    public void testFromDatbaseMethodInvalidParameter() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(4)).thenThrow(new SQLException("xxx"));
        DbNode dbInvalid = DbNode.fromDatabase(resultSet);
        Assert.assertEquals(null, dbInvalid);
    }

}
