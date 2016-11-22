package nl.han.asd.project.client.commonclient.message;

import com.google.inject.Inject;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.graph.IUpdateGraph;
import nl.han.asd.project.client.commonclient.node.ISendData;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.client.commonclient.store.IContactManager;
import nl.han.asd.project.client.commonclient.store.IContactStore;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Service that holds all messages which have not been confirmed yet.
 * Tries to resend messages on a predefined interval.
 *
 * @version 1.0
 */
public class MessageConfirmationService implements IMessageConfirmation {

    public static final int TIMEOUT = 5000;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(MessageConfirmationService.class);
    private static Map<String, RetryMessage> waitingMessages = new HashMap<>();
    private volatile boolean isRunning = true;
    private IMessageBuilder messageBuilder;
    private IContactStore contactStore;
    private ISendData sendData;
    private IUpdateGraph updateGraph;
    private IContactManager contactManager;

    @Inject
    public MessageConfirmationService(IMessageBuilder messageBuilder,
            IContactStore contactStore, ISendData sendData,
            IUpdateGraph updateGraph, IContactManager contactManager) {
        this.messageBuilder = messageBuilder;
        this.contactStore = contactStore;
        this.sendData = sendData;
        this.updateGraph = updateGraph;
        this.contactManager = contactManager;
        new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.trace("message confirmation service started");

                while (isRunning) {
                    try {
                        checkAllMessages();
                        Thread.sleep(TIMEOUT);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
                waitingMessages.clear();
            }
        }).start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageSent(String id, Message message, Contact contact) {
        waitingMessages.put(id, new RetryMessage(id, message, contact));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageConfirmationReceived(String id) {
        waitingMessages.remove(id);
    }

    private void checkAllMessages() {
        for (RetryMessage retryMessage : waitingMessages.values()) {
            if (retryMessage.shouldRetry()) {
                updateGraph.updateGraph();
                contactManager.updateAllContactInformation();

                HanRoutingProtocol.Message.Builder builder = HanRoutingProtocol.Message
                        .newBuilder();
                builder.setId(retryMessage.id);
                builder.setSender(contactStore.getCurrentUser().asContact()
                        .getUsername());
                builder.setText(retryMessage.message.getText());
                builder.setTimeSent(System.currentTimeMillis() / 1000L);

                try {
                    HanRoutingProtocol.MessageWrapper messageWrapper = messageBuilder
                            .buildMessage(builder.build(), contactStore
                                    .findContact(
                                            retryMessage.contact.getUsername()))
                            .getMessageWrapper();
                    sendData.sendData(messageWrapper);
                } catch (MessageNotSentException | IndexOutOfBoundsException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                retryMessage.attemptCount++;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        isRunning = false;

        LOGGER.trace("message confirmation service stopped");
    }
}
