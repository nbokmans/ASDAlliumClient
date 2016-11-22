package nl.han.asd.project.client.commonclient.message;

import com.google.inject.Inject;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.graph.IUpdateGraph;
import nl.han.asd.project.client.commonclient.node.ISendData;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.client.commonclient.store.IContactManager;
import nl.han.asd.project.client.commonclient.store.IContactStore;
import nl.han.asd.project.client.commonclient.store.IMessageStore;
import nl.han.asd.project.commonservices.encryption.IEncryptionService;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static nl.han.asd.project.protocol.HanRoutingProtocol.*;

/**
 *  Processes messages by decrypting their content and storing them in a MessageStore.
 *
 *  @version 1.0
 */
public class MessageProcessingService
        implements IReceiveMessage, ISendMessage, ISubscribeMessageReceiver {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MessageProcessingService.class);
    private static List<IMessageReceiver> receivers = new ArrayList<>();
    private final IMessageStore messageStore;
    private final ISendData nodeConnectionService;
    private final IEncryptionService encryptionService;
    private IMessageConfirmation messageConfirmationService;
    private IContactStore contactStore;
    private IMessageBuilder messageBuilder;
    private IUpdateGraph updateGraph;
    private IContactManager contactManager;

    @Inject
    public MessageProcessingService(IMessageStore messageStore,
            IEncryptionService encryptionService,
            ISendData nodeConnectionService,
            IMessageConfirmation messageConfirmationService,
            IContactStore contactStore, IMessageBuilder messageBuilder,
            IUpdateGraph updateGraph, IContactManager contactManager) {
        this.messageStore = messageStore;
        this.encryptionService = encryptionService;
        this.nodeConnectionService = nodeConnectionService;
        this.messageConfirmationService = messageConfirmationService;
        this.contactStore = contactStore;
        this.messageBuilder = messageBuilder;
        this.updateGraph = updateGraph;
        this.contactManager = contactManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processIncomingMessage(GeneratedMessage messageWrapper) {
        if (messageWrapper instanceof MessageConfirmation) {
            processMessageConfirmation((MessageConfirmation) messageWrapper);
        } else if (messageWrapper instanceof HanRoutingProtocol.Message) {
            processMessage((HanRoutingProtocol.Message) messageWrapper);
        } else {
            LOGGER.error(
                    "Packet didn't contain a MessageConfirmation nor a Message but {}.",
                    messageWrapper);
        }
    }

    private void processMessage(HanRoutingProtocol.Message messageWrapper) {
        HanRoutingProtocol.Message message = messageWrapper;
        Message internalMessage = Message.fromProtocolMessage(message,
                contactStore.getCurrentUser().asContact());
        messageStore.addMessage(internalMessage);

        for (IMessageReceiver receiver : receivers) {
            if (receiver != null) {
                receiver.receivedMessage(internalMessage);
            }
        }
        confirmMessage(message);
    }

    private void processMessageConfirmation(MessageConfirmation messageWrapper) {
        MessageConfirmation messageConfirmation = messageWrapper;
        messageStore.confirmMessage(messageConfirmation.getConfirmationId());
        messageConfirmationService.messageConfirmationReceived(messageConfirmation.getConfirmationId());

        for (IMessageReceiver receiver : receivers) {
            if (receiver != null) {
                receiver.confirmedMessage(
                        messageConfirmation.getConfirmationId());
            }
        }
    }

    private Wrapper decryptEncryptedWrapper(
            MessageWrapper encryptedMessageWrapper)
            throws InvalidProtocolBufferException {
        // TODO: Enable encryption

        //        byte[] wrapperBuffer = encryptionService
        //                .decryptData(encryptedMessageWrapper.getData().toByteArray());
        return Wrapper.parseFrom(encryptedMessageWrapper.getData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String sendMessage(Message message, Contact contact) {
        updateGraph.updateGraph();
        contactManager.updateAllContactInformation();

        contact = contactStore.findContact(contact.getUsername());

        HanRoutingProtocol.Message.Builder builder = HanRoutingProtocol.Message
                .newBuilder();
        builder.setId(generateUniqueMessageId(contact.getUsername()));
        builder.setSender(
                contactStore.getCurrentUser().asContact().getUsername());
        builder.setText(message.getText());
        builder.setTimeSent(System.currentTimeMillis());

        message.setMessageId(builder.getId());
        message.setTimeSent(builder.getTimeSent());
        messageConfirmationService.messageSent(builder.getId(), message, contact);

        MessagePathBundle bundle = messageBuilder.buildMessage(builder.build(), contact);

        messageStore.addMessageWithPath(message, bundle.getPath());

        try {
            nodeConnectionService.sendData(bundle.getMessageWrapper());
        } catch (MessageNotSentException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return builder.getId();
    }

    private String generateUniqueMessageId(String seed) {
        String id = seed + String.valueOf(System.currentTimeMillis());

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(id.getBytes());
            byte[] digest = messageDigest.digest();
            id = String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return id;
    }

    private void confirmMessage(HanRoutingProtocol.Message message) {
        Contact sender = contactStore.findContact(message.getSender());

        if (sender == null) {
            contactStore.addContact(message.getSender());
        }

        updateGraph.updateGraph();
        contactManager.updateAllContactInformation();

        sender = contactStore.findContact(message.getSender());

        MessageConfirmation.Builder builder = MessageConfirmation.newBuilder();
        builder.setConfirmationId(message.getId());

        MessageWrapper messageWrapper = messageBuilder
                .buildMessage(builder.build(), sender).getMessageWrapper();
        try {
            nodeConnectionService.sendData(messageWrapper);
        } catch (MessageNotSentException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe(IMessageReceiver receiver) {
        receivers.add(receiver);
    }
}
