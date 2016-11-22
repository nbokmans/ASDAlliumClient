package nl.han.asd.project.client.commonclient.message;

import nl.han.asd.project.client.commonclient.store.Contact;

public interface IMessageConfirmation extends AutoCloseable {
    /**
     * Add a sent message to a list of messages awaiting confirmation.
     * Messages added here will be retried periodically, with a larger interval on every retry.
     *
     * @param id Message id
     * @param message The sent message
     * @param contact The user to which the message was sent
     */
    public void messageSent(String id, Message message, Contact contact);

    /**
     * Remove a confirmed message from a list of messages awaiting confirmation.
     * The message will no longer be retried.
     *
     * @param id Message id
     */
    public void messageConfirmationReceived(String id);
}
