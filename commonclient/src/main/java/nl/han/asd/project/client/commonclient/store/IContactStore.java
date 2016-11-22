package nl.han.asd.project.client.commonclient.store;

import java.sql.SQLException;
import java.util.List;

/**
 * Define methods to add/remove and fetch
 * contacts from storage.
 *
 * @version 1.0
 */
public interface IContactStore extends AutoCloseable {

    /**
     * Adds new contact into contactstore.
     *  @param username username of contact
     */
    void addContact(String username);

    /**
     * Removes contact from contactStore.
     *
     * @param username username of to be deleted contact
     */
    void removeContact(String username);

    /**
     * Get a list of all the contacts in the contactstore.
     *
     * @return a list of all the contacts
     */
    List<Contact> getAllContacts();

    /**
     * Finds a contact in the contactstore by username.
     *
     * @param username username of contact.
     * @return <tt>Contact</tt> if found, <tt>null</tt> otherwise.
     */
    Contact findContact(String username);

    /**
     * Setter for currentUser.
     *
     * @param currentUser the current user that is logged in
     */
    void setCurrentUser(CurrentUser currentUser);

    /**
     * Getter for currentUser.
     *
     * @return current user that is logged in
     */
    CurrentUser getCurrentUser();

    /**
     * Update the information of a single Contact based on a getClientGroup response
     *
     * @param user Username of the user
     * @param publicKey Public key
     * @param online Current online status
     * @param connectNodes List of node ID's the client is connected to
     */
    void updateUserInformation(String user, byte[] publicKey, boolean online, List<String> connectNodes);

    /**
     * Initiate the store using the provided username and password.
     *
     * @param username The user's username.
     * @param password The user's password.
     */
    void init(String username, String password) throws SQLException;
}
