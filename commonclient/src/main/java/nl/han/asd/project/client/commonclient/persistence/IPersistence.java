package nl.han.asd.project.client.commonclient.persistence;

import nl.han.asd.project.client.commonclient.database.IDatabase;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.path.DbPath;
import nl.han.asd.project.client.commonclient.store.Contact;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines an interface for persistence functions.
 *
 * @version 1.0
 */
public interface IPersistence extends AutoCloseable {

    /**
     * Deletes a message from the database.
     *
     * @param id The id of the message to remove from the database.
     * @return <tt>true</tt> if the message was deleted successfully, <tt>false</tt> otherwise.
     */
    boolean deleteMessage(final int id);

    /**
     * Saves a message in the database.
     *
     * @param message The message to save.
     * @return <tt>true</tt> if the message was saved successfully, <tt>false</tt> otherwise.
     */
    boolean saveMessage(final Message message);

    /**
     * Confirms a message in the database
     * @param messageId The message to confirm
     */
    void confirmMessage(String messageId);

    /**
     * Returns a list of all messages.
     *
     * @return A list of all messages.
     */
    List<Message> getAllMessages();

    /**
     * Returns a map of all messages, separated per contact.
     *
     * @return A map of all messages, separated per contact.
     */
    HashMap<Contact, List<Message>> getAllMessagesPerContact();

    /**
     * Adds a contact to the database.
     *
     * @param username The username of the contact to add to the database.
     * @return <tt>true</tt> if the contact was added successfully, <tt>false</tt> otherwise.
     */
    boolean addContact(final String username);

    /**
     * Deletes a contact from the database.
     *
     * @param username The username of the contact to delete from the database.
     * @return <tt>true</tt> if the contact was deleted successfully, <tt>false</tt> otherwise.
     */
    boolean deleteContact(final String username);

    /**
     * Deletes all contacts from the database.
     *
     * @return <tt>true</tt> if all contacts are successfully deleted, <tt>false</tt> otherwise.
     */
    boolean deleteAllContacts();

    /**
     * Returns a map of all contacts.
     *
     * @return A map of all contacts.
     */
    Map<String, Contact> getContacts();

    /**
     * Retuns the currently open database connection.
     *
     * @return The currently open database connection.
     */
    IDatabase getDatabase();

    /**
     * Fetches a list of all saved scripts from the database.
     *
     * @return A map containing all scripts, with the script name as the key, and the content of the script as the value.
     */
    Map<String, String> getScripts();

    /**
     * Deletes a script from the database by name.
     *
     * @param scriptName The name of the script to remove.
     * @return <tt>true</tt> if the deletion was successful, <tt>false</tt> otherwise.
     */
    boolean deleteScript(final String scriptName);

    /**
     * Adds a scripts to the database.
     *
     * @param scriptName The name of the script to add to the database.
     * @return <tt>true</tt> if the insertion was successful, <tt>false</tt> otherwise.
     */
    boolean addScript(final String scriptName);

    /**
     * Gets the path that is saved in the database for a message.
     *
     * @param messageId The id of the message to look the path up for.
     * @return The path.
     */
    DbPath getDatabasePath(String messageId);

    /**
     * Adds a path to the database.
     * @param messageId The message id of the message to add a new node to the path for.
     * @param nodeId The node id to add to the path.
     * @param sequenceNumber The sequence number of this node in the path.
     * @return <tt>true</tt> if the query was executed succesfully, <tt>false</tt> otherwise.
     */
    boolean addPath(String messageId, String nodeId, int sequenceNumber);

    /**
     * Deletes a path from the database
     * @param messageId The message id to remove the path for.
     * @return <tt>true</tt> if the query was executed succesfully, <tt>false</tt> otherwise.
     */
    boolean deletePath(String messageId);

    /** Gets a list containing the names of all saved scripts.
     *
     * @return <tt>List<String></tt> containing the names of all saved scripts.
     */
    List<String> getAllScriptNames();

    /**
     * Gets the content of a script.
     *
     * @param scriptName The name of the script of which the content will be fetched.
     * @return <tt>String</tt> containing the content of a script.
     */
    String getScriptContent(String scriptName);

    /**
     * Updates the content of a script.
     */
    void updateScript(String scriptName, String scriptContent);

    /**
     * @param username The user's username.
     * @param password The user's password.
     */
    void init(String username, String password) throws SQLException;

    /**
     * Returns the message of the corresponding id
     * @param messageId the message you want to retreive
     * @return the message matching the id or null when no message found
     */
    Message getMessage(String messageId);

    enum DateFormat {
        TIMESTAMP(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        public final SimpleDateFormat format;

        DateFormat(SimpleDateFormat format) {
            this.format = format;
        }
    }
}
