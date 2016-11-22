package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.commonservices.internal.utility.Check;

/**
 * This class implements the current user.
 * The current user is created when the user has successfully logged in at the master server.
 *
 * @version 1.0
 */
public class CurrentUser {
    private String secretHash;
    private Contact contact;

    /**
     * Constructor of CurrentUser.
     *
     * @param username username of user
     * @param publicKey publicKey of user
     * @param secretHash hash of user used for identification to master server
     *
     * @throws IllegalArgumentException if secretHash is null
     */
    public CurrentUser(String username, byte[] publicKey, String secretHash) {
        contact = new Contact(username, publicKey);
        this.secretHash = Check.notNull(secretHash, "secretHash");
    }

    /**
     * Retrieves the secretHash from the class.
     *
     * @return The secretHash used to identify the current user
     */
    public String getSecretHash() {
        return secretHash;
    }

    /**
     * Retrieves the current user as a contact.
     *
     * @return current user that is logged in as contact
     */
    public Contact asContact() {
        return contact;
    }
}
