package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.message.Message;

import java.sql.SQLException;
import java.util.List;

public interface IMessageStore extends AutoCloseable {
    /**
     * Add message to MessageStore.
     *
     * @param message Message to be added.
     */
    void addMessage(Message message);

    /**
     * Adds a message to the MessageStore with the used path.
     *
     * @param message Message to be added.
     * @param path Path that was used to send the message.
     */
    void addMessageWithPath(Message message, List<Node> path);

    /**
     * Gets a path from the given message id.
     *
     * @param messageId The identifier we need to check on.
     * @return The path of the message, note that this will only return the identifiers of the nodes.
     */
    List<String> getPathByMessageId(String messageId);

    /**
     * Returns all messages for a certain user after a certain dateTime.
     *
     * @param dateTime unix time stamp.
     * @return an arrayList of messages.
     */
    Message[] getMessagesAfterDate(long dateTime);

    /**
     * Get all messages sent to and received from a user.
     *
     * @param username User to get messages from.
     *
     * @return List of messages.
     */
    List<Message> getMessagesFromUser(final String username);

    /**
     * Get's the message that correspondends to the message id
     * @param messageId the id of the message that has to be found
     * @return the message with the id or null when not found
     */
    Message getMessage(final String messageId);

    /**
     * Initiate the store using the provided username and password.
     *
     * @param username The user's username.
     * @param password The user's password.
     */
    void init(String username, String password) throws SQLException;

    /**
     * Confirm message as received
     * @param messageId the id of the message that is confirmed
     */
    void confirmMessage(String messageId);
}
