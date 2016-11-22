package nl.han.asd.project.client.commonclient.message;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.path.IGetMessagePath;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.commonservices.encryption.IEncryptionService;
import nl.han.asd.project.protocol.HanRoutingProtocol;

import java.util.List;

/**
 * Service that builds a message packet based on a chosen path.
 *
 * @version 1.0
 */
public class MessageBuilderService implements IMessageBuilder {
    private static final int MINIMAL_HOPS = 1;

    private IGetMessagePath getPath;
    private IEncryptionService encryptionService;

    @Inject
    public MessageBuilderService(IGetMessagePath getPath, IEncryptionService encryptionService) {
        this.getPath = getPath;
        this.encryptionService = encryptionService;
    }

    @Override
    public <T extends GeneratedMessage> MessagePathBundle buildMessage(T generatedMessage , Contact contactReceiver) {

        HanRoutingProtocol.Wrapper.Builder wrapperBuilder = HanRoutingProtocol.Wrapper.newBuilder();
        MessagePathBundle bundle = new MessagePathBundle();

        wrapperBuilder.setData(generatedMessage.toByteString());
        List<Node> path = getPath.getPath(MINIMAL_HOPS, contactReceiver);
        bundle.setPath(path);

        if(generatedMessage.getClass() == HanRoutingProtocol.Message.class){
            wrapperBuilder.setType(HanRoutingProtocol.Wrapper.Type.MESSAGE);
        }
        else if(generatedMessage.getClass() == HanRoutingProtocol.MessageConfirmation.class){
            wrapperBuilder.setType(HanRoutingProtocol.Wrapper.Type.MESSAGECONFIRMATION);
        }
        else{
            throw new IllegalArgumentException();
        }

        byte[] firstLayer = buildFirstMessagePackageLayer(path.get(0), wrapperBuilder.build(), contactReceiver);

        path.remove(0);

        bundle.setMessageWrapper(buildLastMessagePackageLayer(path.get(path.size() - 1), buildMessagePackageLayer(firstLayer, path)));

        return bundle;

    }

    /**
     * Deepest layer in final message package
     * @param node contains information about the next hop in the path.
     * @param wrapper contains information about the message that needs to be send.
     * @return encrypted data from the first layer that is build
     */
    private byte[] buildFirstMessagePackageLayer(Node node, HanRoutingProtocol.Wrapper wrapper, Contact contactReceiver) {
        HanRoutingProtocol.MessageWrapper.Builder messageWrapperBuilder = HanRoutingProtocol.MessageWrapper.newBuilder();

        messageWrapperBuilder.setUsername(contactReceiver.getUsername());
        messageWrapperBuilder.setIPaddress(node.getIpAddress());
        messageWrapperBuilder.setPort(node.getPort());
        messageWrapperBuilder.setData(wrapper.toByteString());

        HanRoutingProtocol.Wrapper.Builder wrapperBuilder = HanRoutingProtocol.Wrapper.newBuilder();
        wrapperBuilder.setType(HanRoutingProtocol.Wrapper.Type.MESSAGEWRAPPER);
        wrapperBuilder.setData(messageWrapperBuilder.build().toByteString());

        // TODO: Enable encryption

        return wrapperBuilder.build().toByteArray();
//        return encryptionService.encryptData(messageWrapperBuilder.build().toByteArray(), node.getPublicKey());
    }


    private HanRoutingProtocol.MessageWrapper buildLastMessagePackageLayer(Node node, byte[] data) {
        HanRoutingProtocol.MessageWrapper.Builder messageWrapperBuilder = HanRoutingProtocol.MessageWrapper.newBuilder();

        messageWrapperBuilder.setIPaddress(node.getIpAddress());
        messageWrapperBuilder.setPort(node.getPort());
        messageWrapperBuilder.setData(ByteString.copyFrom(data));

        return messageWrapperBuilder.build();
    }

    private byte[] buildMessagePackageLayer(byte[] message, List<Node> remainingPath) {
        if (remainingPath.size() == 1) {
            return message;
        }
        HanRoutingProtocol.MessageWrapper.Builder builder = HanRoutingProtocol.MessageWrapper.newBuilder();

        Node node = remainingPath.get(0);
        builder.setIPaddress(node.getIpAddress());
        builder.setPort(node.getPort());
        builder.setData(ByteString.copyFrom(message));

        // TODO: Enable encryption

//        byte[] encryptedMessage = encryptionService
//                .encryptData(node.getPublicKey(),builder.build().toByteArray());
        ByteString encryptedMessage = builder.build().toByteString();

        HanRoutingProtocol.Wrapper.Builder wrapperBuilder = HanRoutingProtocol.Wrapper.newBuilder();
        wrapperBuilder.setType(HanRoutingProtocol.Wrapper.Type.MESSAGEWRAPPER);
        wrapperBuilder.setData(encryptedMessage);

        remainingPath.remove(0);

        return buildMessagePackageLayer(wrapperBuilder.build().toByteArray(), remainingPath);
    }
}
