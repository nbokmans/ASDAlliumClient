package nl.han.asd.project.client.commonclient.store;

import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.path.DbNode;
import nl.han.asd.project.client.commonclient.path.DbPath;
import nl.han.asd.project.client.commonclient.persistence.IPersistence;
import nl.han.asd.project.commonservices.internal.utility.Check;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides functionality to store messages. Also provides functionality to read
 * these messages again.
 *
 * @version 1.0
 */
public class MessageStore implements IMessageStore {
    private IPersistence persistenceService;

    /**
     * Construct a new MessageStore instance.
     *
     * @param persistenceService persistence service used to
     *          write the messages to persistent storage
     */
    @Inject
    public MessageStore(final IPersistence persistenceService) {
        this.persistenceService = Check.notNull(persistenceService, "persistenceService");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(String username, String password) throws SQLException {
        Check.notNull(username, "username");
        Check.notNull(password, "password");

        persistenceService.init(username, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMessageWithPath(Message message, List<Node> path) {
        addMessage(message);
        for (int i = 0; i < path.size(); i++) {
            persistenceService.addPath(message.getMessageId(), path.get(i).getId(), i + 1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override public List<String> getPathByMessageId(String messageId) {
        DbPath path = persistenceService.getDatabasePath(messageId);
        List<DbNode> listOfDbNodes = path.getNodesInPath();

        List<String> listOfNodes = new ArrayList<>(listOfDbNodes.size());
        for (DbNode dbNode : listOfDbNodes) {
            listOfNodes.add(dbNode.getNodeId());
        }

        return listOfNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void confirmMessage(String messageId) {
        persistenceService.confirmMessage(messageId);
    }

    @Override
    public void addMessage(final Message message) {
        Check.notNull(message, "message");

        persistenceService.saveMessage(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override public List<Message> getMessagesFromUser(String username) {
        Check.notNull(username, "username");

        Map<Contact, List<Message>> messagesPerContact = persistenceService
                .getAllMessagesPerContact();
        return messagesPerContact.get(new Contact(username));
}

    /**
     * {@inheritDoc}
     */
    @Override
    public Message getMessage(String messageId) {
        return persistenceService.getMessage(messageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message[] getMessagesAfterDate(long dateTimeSince) {
        if (dateTimeSince <= 0 || dateTimeSince > System.currentTimeMillis()) {
            throw new IllegalArgumentException(
                    "DateTimeSince cannot be smaller then 0 or bigger then the current time.");
        }

        Date dateSince = new Date(dateTimeSince);

        Map<Contact, List<Message>> messagesPerContact = persistenceService
                .getAllMessagesPerContact();

        List<Message> messagesAfterDate = new ArrayList<>();

        for (List<Message> messages : messagesPerContact.values()) {
            for (Message message : messages) {
                if (message.getMessageTimestamp().compareTo(dateSince) > 0) {
                    messagesAfterDate.add(message);
                }
            }
        }
        return messagesAfterDate.toArray(new Message[messagesAfterDate.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        persistenceService.close();
    }
}
