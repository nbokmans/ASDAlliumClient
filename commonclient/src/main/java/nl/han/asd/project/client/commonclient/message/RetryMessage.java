package nl.han.asd.project.client.commonclient.message;

import nl.han.asd.project.client.commonclient.store.Contact;

/**
 * Contains all data needed to send a message repeatedly using the MessageConfirmationService.
 *
 * @version 1.0
 */
public class RetryMessage {
    public String id;
    public Message message;
    public Contact contact;
    public long lastAttempt;
    public int attemptCount = 1;

    public static final int TIMEOUT = 10;

    public RetryMessage(String id, Message message, Contact contact) {
        this.id = id;
        this.message = message;
        this.contact = contact;
        lastAttempt = System.currentTimeMillis() / 1000L;
    }

    /**
     * Determines if the message should already be retried.
     *
     * @return If the message should be retried.
     */
    public boolean shouldRetry() {
        return (System.currentTimeMillis() / 1000L - lastAttempt) > TIMEOUT * attemptCount * attemptCount;
    }
}
