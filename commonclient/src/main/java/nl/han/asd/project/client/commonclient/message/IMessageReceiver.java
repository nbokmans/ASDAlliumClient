package nl.han.asd.project.client.commonclient.message;

public interface IMessageReceiver {
    /**
     * Forward a received message
     *
     * @param message The internal message object that is received
     */
    public void receivedMessage(Message message);

    /**
     * Confirm a message is received
     *
     * @param messageId Message Id of the confirmed message
     */
    public void confirmedMessage(String messageId);
}
