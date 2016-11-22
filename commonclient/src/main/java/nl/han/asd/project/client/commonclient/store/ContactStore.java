package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.client.commonclient.graph.IGetVertices;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.persistence.IPersistence;
import nl.han.asd.project.commonservices.internal.utility.Check;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage the current and stored contacts.
 *
 * @version 1.0
 */
public class ContactStore implements IContactStore {
    private final IGetVertices graphManager;
    private IPersistence persistence;
    private CurrentUser currentUser;
    private Map<String, Contact> contacts = new HashMap<>();

    /**
     * Constructor of Contactstore.
     *
     * @param persistence the layer where the store connects and communicates with the database
     */
    @Inject
    public ContactStore(IPersistence persistence, IGetVertices graphManager) {
        this.graphManager = Check.notNull(graphManager, "graphManager");
        this.persistence = Check.notNull(persistence, "persistence");

        contacts = persistence.getContacts();
    }

    @Override
    public void init(String username, String password) throws SQLException {
        persistence.init(username, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContact(String username) {
        if (contacts.containsKey(username)) {
            return;
        }

        contacts.put(username, new Contact(username));
        persistence.addContact(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeContact(String username) {
        if (findContact(username) == null) {
            return;
        }

        contacts.remove(username);
        persistence.deleteContact(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUserInformation(String username, byte[] publicKey, boolean online,
            List<String> connectedNodeIds) {
        Contact oldContact = findContact(username);
        if (oldContact == null) {
            return;
        }

        Contact newContact = new Contact(username, publicKey, online);

        Node[] connectedNodes = new Node[connectedNodeIds.size()];
        int i = 0;
        for (String connectedNodeId : connectedNodeIds) {
            Node node = graphManager.getVertices().get(connectedNodeId);
            connectedNodes[i] = node;
            i++;
        }

        newContact.setConnectedNodes(connectedNodes);
        contacts.put(username, newContact);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Contact> getAllContacts() {
        Map<String, Contact> databaseContacts = persistence.getContacts();

        for (Contact contact : contacts.values()) {
            databaseContacts.put(contact.getUsername(), contact);
        }

        return new ArrayList<>(databaseContacts.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact findContact(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        if (contacts.containsKey(username)) {
            return contacts.get(username);
        }

        return persistence.getContacts().get(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        persistence.close();
        this.currentUser = null;
    }
}
