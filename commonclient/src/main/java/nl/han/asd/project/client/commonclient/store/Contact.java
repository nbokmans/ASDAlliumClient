package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.client.commonclient.graph.Node;

/**
 * The contact which the current user can communicate to.
 *
 * @version 1.0
 */
public class Contact {
    private String username;
    private Node[] connectedNodes;
    private byte[] publicKey;
    private boolean online;

    /**
     * Constructor of Contact.
     *
     * @param username username of contact
     */
    public Contact(String username) {
        this(username, new byte[] {}, false);
    }

    /**
     * Constructor of Contact.
     *
     * @param username username of contact
     * @param publicKey publicKey of contact
     */
    public Contact(String username, byte[] publicKey) {
        this(username, publicKey, false);
    }

    /**
     * Constructor of Contact.
     *
     * @param username username of contact
     * @param publicKey publicKey of contact
     * @param online if the contact is online or not
     */
    public Contact(String username, byte[] publicKey, boolean online) {
        this.username = username;
        this.publicKey = publicKey;
        this.online = online;
    }

    /**
     * Gets contact from database.
     * @param username username of contact
     * @return Contact of current user by username from database.
     */
    public static Contact fromDatabase(String username) {
        return new Contact(username);
    }

    /**
     * Getter for username.
     *
     * @return username of contact
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for connectedNodes.
     *
     * @return an array of nodes where the contact is connected to
     * @throws NoConnectedNodesException when no nodes aren't initialized
     */
    public Node[] getConnectedNodes() throws NoConnectedNodesException {
        if (connectedNodes == null || connectedNodes.length <= 0) {
            throw new NoConnectedNodesException("The connected Nodes from the contactStore are not set");
        }
        return connectedNodes;

    }

    /**
     * Setter for connectedNodes.
     *
     * @param connectedNodes an array of nodes where the contact is connected to
     */
    public void setConnectedNodes(Node[] connectedNodes) {
        this.connectedNodes = connectedNodes;
    }

    /**
     * Setter for publicKey.
     *
     * @param publicKey the publicKey of the contact for encrypting messages.
     */
    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Getter for publicKey.
     *
     * @return the publicKey of the contact for encrypting messages.
     */
    public byte[] getPublicKey() {
        return publicKey;
    }

    /**
     * Getter for online status.
     *
     * @return if the contact is online or not
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Setter for online status.
     *
     * @param online if the contact is online or not
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Returns the contact's username as string.
     *
     * @return the usernaname of the contact as string
     */
    @Override
    public String toString() {
        return getUsername();
    }

    /**
     * Checks if anotherObject is equal of contact.
     *
     * @param anotherObject the to be checked object
     * @return <tt>true</tt> if anotherObject is equal of contact, <tt>false</tt> otherwise
     */
    @Override
    public boolean equals(Object anotherObject) {
        if (anotherObject == null || !(anotherObject instanceof Contact)) {
            return false;
        }
        Contact contact = (Contact) anotherObject;
        return contact.getUsername().equals(getUsername());
    }

    /**
     * Get the hashCode of the contact's username.
     *
     * @return the hashCode of the contact's username.
     */
    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
}
