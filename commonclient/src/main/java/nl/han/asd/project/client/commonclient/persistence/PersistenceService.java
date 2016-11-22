package nl.han.asd.project.client.commonclient.persistence;

import nl.han.asd.project.client.commonclient.database.IDatabase;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.path.DbNode;
import nl.han.asd.project.client.commonclient.path.DbPath;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.commonservices.internal.utility.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a way to communicate with the database.
 *
 * @version 1.0
 */
public class PersistenceService implements IPersistence {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PersistenceService.class);
    private SimpleDateFormat format = IPersistence.DateFormat.TIMESTAMP.format;

    private IDatabase database;
    private boolean initialized = false;

    @Inject
    public PersistenceService(IDatabase database) {
        this.database = Check.notNull(database, "database");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(String username, String password) throws SQLException {
        if (!initialized) {
            LOGGER.trace("persistence service stopped");
            database.init(username, password);
        }
        initialized = true;
    }

    @Override
    public Message getMessage(String messageId) {
        final String query = "SELECT * FROM Message WHERE messageId = ?";
        ResultSet result = null;
        try{
            final PreparedStatement preparedStatement = getDatabase().prepareStatement(query);
            preparedStatement.setString(1, messageId);
            result = preparedStatement.executeQuery();
            result.next();
            preparedStatement.close();
        }
        catch(SQLException e){
            LOGGER.error(e.getMessage(), e);
        }
        if (result != null) {
            return Message.fromDatabase(result);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteMessage(int id) {
        final String query = "DELETE FROM Message WHERE id = ?";
        try {
            final PreparedStatement preparedStatement = getDatabase()
                    .prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveMessage(Message message) {
        final String query = "INSERT INTO Message (messageId, sender, receiver, timestamp, message, confirmed) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            final PreparedStatement preparedStatement = getDatabase()
                    .prepareStatement(query);
            preparedStatement.setString(1, message.getMessageId());
            preparedStatement.setString(2, message.getSender().getUsername());
            preparedStatement.setString(3, message.getReceiver().getUsername());
            preparedStatement.setString(4, message.getFormattedTimestamp());
            preparedStatement.setString(5, message.getText());
            preparedStatement.setBoolean(6, message.isConfirmed());
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void confirmMessage(String messageId) {
        final String query = "UPDATE Message SET confirmed = ? WHERE messageId = ?";
        try{
            final PreparedStatement preparedStatement = getDatabase().prepareStatement(query);
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, messageId);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> getAllMessages() {
        final List<Message> messageList = new ArrayList<>();
        try {
            ResultSet selectMessagesResult = getDatabase()
                    .select("SELECT * FROM Message");

            if (selectMessagesResult == null) {
                return messageList;
            }

            while (selectMessagesResult.next()) {
                messageList.add(Message.fromDatabase(selectMessagesResult));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return messageList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<Contact, List<Message>> getAllMessagesPerContact() {
        final HashMap<Contact, List<Message>> contactMessagesHashMap = new HashMap<>();
        try {
            final ResultSet selectMessagesResult = getDatabase()
                    .select("SELECT * FROM Message ORDER BY timestamp ASC");
            filterMessages(contactMessagesHashMap, selectMessagesResult);
        } catch (final SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return contactMessagesHashMap;
    }

    private void filterMessages(
            final HashMap<Contact, List<Message>> contactMessagesHashMap,
            final ResultSet selectMessagesResult) throws SQLException {
        while (selectMessagesResult != null && selectMessagesResult.next()) {
            final Message message = Message.fromDatabase(selectMessagesResult);

            if (message != null) {
                if (!contactMessagesHashMap.containsKey(message.getSender())) {
                    contactMessagesHashMap
                            .put(message.getSender(), new ArrayList<Message>());
                }

                if (!contactMessagesHashMap
                        .containsKey(message.getReceiver())) {
                    contactMessagesHashMap.put(message.getReceiver(),
                            new ArrayList<Message>());
                }

                contactMessagesHashMap.get(message.getSender()).add(message);
                contactMessagesHashMap.get(message.getReceiver()).add(message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addContact(String username) {
        try {
            executeQuery("INSERT INTO Contact (username) VALUES (?)", username);
            return true;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteContact(String username) {
        try {
            executeQuery("DELETE FROM Contact WHERE username = ?", username);
            return true;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    private void executeQuery(String query, String... parameters)
            throws SQLException {
        PreparedStatement preparedStatement = getDatabase()
                .prepareStatement(query);

        for (int i = 1; i <= parameters.length; i++) {
            preparedStatement.setString(i, parameters[i - 1]);
        }

        preparedStatement.execute();
        preparedStatement.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteAllContacts() {
        try {
            return getDatabase().query("DELETE FROM Contact");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Contact> getContacts() {
        final Map<String, Contact> contactMap = new HashMap<>();
        try {
            ResultSet selectContactsResult = getDatabase()
                    .select("SELECT * FROM Contact");

            if (selectContactsResult == null) {
                return contactMap;
            }

            while (selectContactsResult.next()) {
                contactMap.put(selectContactsResult.getString(2),
                        Contact.fromDatabase(
                                selectContactsResult.getString(2)));
            }

            selectContactsResult.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return contactMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDatabase getDatabase() {
        return database;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getScripts() {
        Map<String, String> scripts = new HashMap<>();
        try {
            ResultSet selectScriptsResult = getDatabase()
                    .select("SELECT * FROM Script");
            while (selectScriptsResult.next()) {
                String scriptName = (String) selectScriptsResult.getObject(2);
                String scriptContent = (String) selectScriptsResult
                        .getObject(3);

                scripts.put(scriptName, scriptContent);
            }
            selectScriptsResult.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return scripts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteScript(String scriptName) {
        final String query = "DELETE FROM Script WHERE scriptName = ?";
        try {
            PreparedStatement statement = getDatabase().prepareStatement(query);
            statement.setString(1, scriptName);
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addScript(String scriptName) {
        final String query = "INSERT INTO Script (scriptName) VALUES (?)";
        try {
            PreparedStatement preparedStatement = getDatabase().prepareStatement(query);
            preparedStatement.setString(1, scriptName);
            preparedStatement.execute();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DbPath getDatabasePath(String messageId) {
        final DbPath dbPath = new DbPath(messageId);
        final String query = "SELECT * FROM Path WHERE messageId = ?";
        try {
            final PreparedStatement preparedStatement = getDatabase()
                    .prepareStatement(query);
            preparedStatement.setString(1, messageId);
            preparedStatement.execute();
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dbPath.addNode(DbNode.fromDatabase(resultSet));
            }
            dbPath.sort();
            preparedStatement.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return dbPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addPath(String messageId, String nodeId,
                           int sequenceNumber) {
        try {
            return getDatabase().query(String
                    .format("INSERT INTO Path (messageId, nodeId, sequenceNumber) VALUES('%s', '%s', %d)",
                            messageId, nodeId, sequenceNumber));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deletePath(String messageId) {
        try {
            return getDatabase().query(String.format("DELETE FROM Path WHERE messageId = '%s'", messageId));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllScriptNames() {
        List<String> scripts = new ArrayList<>();
        try {
            ResultSet selectScriptsResult = getDatabase()
                    .select("SELECT scriptName FROM Script");
            while (selectScriptsResult.next()) {
                String scriptName = (String) selectScriptsResult.getObject(1);
                scripts.add(scriptName);
            }
            selectScriptsResult.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return scripts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScriptContent(String scriptName) {
        String result = "";
        final String query = "SELECT scriptContent FROM Script WHERE scriptName = ?";
        try {
            PreparedStatement preparedStatement = getDatabase().prepareStatement(query);
            preparedStatement.setString(1, scriptName);
            ResultSet selectScriptsResult = getDatabase().select(query);
            result = (String) selectScriptsResult.getObject(1);

            selectScriptsResult.close();

        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateScript(String scriptName, String scriptContent) {
        final String query = "UPDATE Script SET scriptContent = ? WHERE scriptName = ?";
        try {
            PreparedStatement preparedStatement = getDatabase().prepareStatement(query);
            preparedStatement.setNString(1, scriptContent);
            preparedStatement.setString(2, scriptName);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        LOGGER.debug("persistence service stopped");
        if (initialized) {
            database.close();
        }
        initialized = false;
    }

}
