package nl.han.asd.project.client.commonclient.path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class used to store information about a node inside the path that was saved in the database.
 */
public class DbNode implements Comparable<DbNode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbNode.class);

    private String nodeId;
    private int sequenceNumber;

    /**
     *  Initialises the class.
     */
    public DbNode() { }

    /**
     * Initialises the class.
     *
     * @param nodeId Id to hold.
     * @param sequenceNumber SequenceNumber of the NodeId in the path.
     */
    public DbNode(String nodeId, int sequenceNumber) {
        this.nodeId = nodeId;
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Returns an new instance of DbNode matching the ResultSet
     *
     * ResultSet should contain the NodeId on column index 3, as string.
     * ResultSet should contain the SequenceNumber on column index 4, as integer.
     *
     * @param resultSet ResultSet to get the data from.
     * @return A instance of DbNode or null if it failed to construct the class.
     */
    public static DbNode fromDatabase(ResultSet resultSet) {
        try {
            final String nodeId = resultSet.getString(3);
            final int sequenceNumber = resultSet.getInt(4);
            return new DbNode(nodeId, sequenceNumber);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }


    public String getNodeId() {
        return nodeId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public boolean equals(DbNode anotherObject) {
        if (!(anotherObject instanceof DbNode)) {
            return false;
        }
        final DbNode otherDbNode = (DbNode) anotherObject;
        return otherDbNode.getNodeId().equals(getNodeId())
                && otherDbNode.getSequenceNumber() == getSequenceNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(DbNode dbNode) {
        if (dbNode != null) {
            if (dbNode.getSequenceNumber() > getSequenceNumber()) {
                return -1;
            } else if (dbNode.getSequenceNumber() < getSequenceNumber()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            LOGGER.info("Node was null.");
            throw new NullPointerException("Node to compare to is null!");
        }
    }
}
