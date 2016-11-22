package nl.han.asd.project.client.commonclient.message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.han.asd.project.client.commonclient.persistence.IPersistence;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.protocol.HanRoutingProtocol;

public class Message {
    private static final Logger LOGGER = LoggerFactory.getLogger(Message.class);

    private static final SimpleDateFormat format = IPersistence.DateFormat.TIMESTAMP.format;

    public static final int DEFAULT_ID = -1;

    public static final int COLUMN_ID = 1;
    public static final int COLUMN_MESSAGE_ID = 2;
    public static final int COLUMN_SENDER = 3;
    public static final int COLUMN_RECEIVER = 4;
    public static final int COLUMN_TIMESTAMP = 5;
    public static final int COLUMN_MESSAGE = 6;
    public static final int COLUMN_CONFIRMED = 7;
    private static final boolean DFEAULT_CONFIRMED = false;

    private String messageId;
    private int databaseId;

    private String text;
    private Contact sender;
    private Contact receiver;
    private Date timestamp;
    private boolean confirmed;

    /**
     * Constructs a new Message object without a database ID.
     *
     * @param sender The message's sender.
     * @param receiver The message recipient.
     * @param timestamp Timestamp of when the message was sent.
     * @param text The actual message.
     */
    public Message(Contact sender, Contact receiver, Date timestamp, String text) {
        this(DEFAULT_ID, sender, receiver, timestamp, text, "");
    }

    /**
     * Constructs a new Message object without a database ID.
     *
     * @param sender The message's sender.
     * @param receiver The message recipient.
     * @param timestamp Timestamp of when the message was sent.
     * @param text The actual message.
     * @param messageId The id of the message
     */
    public Message(Contact sender, Contact receiver, Date timestamp, String text, String messageId) {
        this(DEFAULT_ID, sender, receiver, timestamp, text, messageId, DFEAULT_CONFIRMED);
    }

    public Message(int id, Contact sender, Contact receiver, Date timestamp, String text, String messageId) {
        this(id, sender, receiver, timestamp, text, messageId, DFEAULT_CONFIRMED);
    }

    /**
     * Constructs a new Message object with a database ID.
     *
     * @param id The database id.
     * @param sender The message's sender.
     * @param receiver The message recipient.
     * @param timestamp Timestamp of when the message was sent.
     * @param text The actual message.
     */
    public Message(int id, Contact sender, Contact receiver, Date timestamp, String text, String messageId,
            boolean confirmed) {
        databaseId = id;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.text = text;
        this.messageId = messageId;
        this.confirmed = confirmed;
    }

    /**
     * Creates a new Message object from a database result.
     *
     * @param result The resultset (result of a database query) to parse a Message from.
     * @return A new Message object from a database result.
     */
    public static Message fromDatabase(ResultSet result) {
        try {
            final int id = (Integer) result.getObject(COLUMN_ID);
            final String messageId = (String) result.getObject(COLUMN_MESSAGE_ID);
            final Contact sender = Contact.fromDatabase((String) result.getObject(COLUMN_SENDER));
            final Contact receiver = Contact.fromDatabase((String) result.getObject(COLUMN_RECEIVER));
            final Date timestamp = format.parse(format.format(result.getTimestamp(COLUMN_TIMESTAMP)));
            final String message = (String) result.getObject(COLUMN_MESSAGE);
            final boolean confirmed = (Boolean) result.getObject(COLUMN_CONFIRMED);
            return new Message(id, sender, receiver, timestamp, message, messageId, confirmed);
        } catch (SQLException | ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Create an internal message based on a protocol message.
     *
     * @param protocolMessage Received message.
     * @param receiver User which received the message.
     *
     * @return Internal message object which contains the same information as the original protocol message.
     */
    public static Message fromProtocolMessage(HanRoutingProtocol.Message protocolMessage, Contact receiver) {
        final String id = protocolMessage.getId();
        final Contact sender = Contact.fromDatabase(protocolMessage.getSender());
        final String text = protocolMessage.getText();
        final Date timestamp = new Date(protocolMessage.getTimeSent());
        return new Message(sender, receiver, timestamp, text, id);
    }

    /**
     * Return's if the message has been confirmed
     * @return the confirmation
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the message's sender.
     * @return The message's sender.
     */
    public Contact getSender() {
        return sender;
    }

    /**
     * Returns the actual message.
     * @return The actual message.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the message ID.
     * @return The message ID.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Returns the message timestamp.
     * @return A timestamp of when the message was sent.
     */
    public Date getMessageTimestamp() {
        return timestamp;
    }

    /**
     * Returns the message's recipient.
     * @return The message's recipient.
     */
    public Contact getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return "Message[messageId = " + messageId + ", sender=" + sender.getUsername() + ", receiver="
                + receiver.getUsername() + ", timestamp=" + timestamp + ", text=" + text + ", confirmed=" + confirmed
                + "]";
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (anotherObject == null || !(anotherObject instanceof Message)) {
            return false;
        }
        final Message otherMessage = (Message) anotherObject;
        return getMessageId().equals(otherMessage.getMessageId());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMessageId()).append(getSender()).append(getText())
                .append(getReceiver()).toHashCode();
    }

    /**
     * Returns the database ID for this Message object.
     * @return The database ID for this Message object.
     */
    public int getDatabaseId() {
        return databaseId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setTimeSent(long timeSent) {
        this.timestamp = new Date(timeSent);
    }

    /**
     * Get the timestamp formatted
     * according to IPersistence.DateFormat.TIMESTAMP.format.
     *
     * @return the formatted timestamp
     */
    public String getFormattedTimestamp() {
        return format.format(timestamp);
    }
}