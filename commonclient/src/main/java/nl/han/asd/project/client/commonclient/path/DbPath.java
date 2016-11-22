package nl.han.asd.project.client.commonclient.path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class that holds an list of DbNode's that represent a path.
 */
public class DbPath {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbPath.class);
    private List<DbNode> nodesInPath;
    private String messageId;

    /**
     * Initialises the class.
     * @param messageId MessageID the path belongs to.
     */
    public DbPath(String messageId) {
        this.messageId = messageId;
        nodesInPath = new ArrayList<>();
    }

    /**
     * Gets the path from the database.
     *
     * @param messageId Message Id to look for.
     * @param resultSet ResultSet to use.
     * @return A DbPath, if found.
     */
    public static DbPath fromDatabase(final String messageId,
            final ResultSet resultSet) {
        final DbPath dbPath = new DbPath(messageId);
        try {
            while (resultSet.next()) {
                dbPath.addNode(DbNode.fromDatabase(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        dbPath.sort();
        return dbPath;
    }

    /**
     * Returns the path.
     *
     * @return The path.
     */
    public List<DbNode> getNodesInPath() {
        return nodesInPath;
    }

    /**
     * Adds a DbNode to the list.
     *
     * @param node DbNode to add to the list.
     */
    public void addNode(final DbNode node) {
        nodesInPath.add(node);
    }

    /**
     * Sorts the path using the compareTo implementation of the DbNode class.
     */
    public void sort() {
        Collections.sort(nodesInPath);
    }

}
