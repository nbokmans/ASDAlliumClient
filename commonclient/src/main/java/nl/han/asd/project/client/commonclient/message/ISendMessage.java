package nl.han.asd.project.client.commonclient.message;

import nl.han.asd.project.client.commonclient.store.Contact;

public interface ISendMessage {
    /**
     * Send a message to a user.
     *
     * @param message Message to be sent.
     * @param contact User to send the message to.
     * @return The MessageID as it was added to the database or NULL if the message was not send.
     */
    public String sendMessage(Message message, Contact contact);
}
