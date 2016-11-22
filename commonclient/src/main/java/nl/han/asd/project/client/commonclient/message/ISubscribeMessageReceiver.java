package nl.han.asd.project.client.commonclient.message;

public interface ISubscribeMessageReceiver {
    /**
     * Subscribe to any messages received
     *
     * @param receiver An instance of IMessageReceiver that will be triggered on received messages
     */
    public void subscribe(IMessageReceiver receiver);
}
