package nl.han.asd.project.client.commonclient.message;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.master.IGetGraphUpdates;
import nl.han.asd.project.client.commonclient.path.IGetMessagePath;
import nl.han.asd.project.client.commonclient.store.Contact;
import nl.han.asd.project.client.commonclient.store.IMessageStore;
import nl.han.asd.project.commonservices.encryption.IEncryptionService;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * @author Julius
 * @version 2.0
 * @since 15/04/1996
 */
@RunWith(MockitoJUnitRunner.class) public class MessageBuilderServiceTest {

    private final String CONTACTRECIEVERUSERNAME = "Bob";
    @Mock private IGetGraphUpdates updatedGraphMock;
    @Mock private ISendMessage sendMessage;
    @Mock private IMessageStore messageStore;
    @Mock private IGetMessagePath pathDeterminationService;
    @Mock private IEncryptionService encrypt;
    private MessageBuilderService messageBuilderService;
    private UUID messageId;
    private Contact contactreciever;
    private List<Node> path;

    @Before
    public void setUp() throws Exception {
        messageBuilderService = new MessageBuilderService(
                pathDeterminationService, encrypt);

        messageId = UUID.randomUUID();

        contactreciever = Mockito.mock(Contact.class);

        path = new ArrayList<>(Arrays.asList(
                new Node("NODE_ID_1", "192.168.2.1", 1234,
                        "123456789".getBytes()),
                new Node("NODE_ID_2", "192.168.2.2", 1234,
                        "123456789".getBytes()),
                new Node("NODE_ID_3", "192.168.2.3", 1234,
                        "123456789".getBytes())));

        when(contactreciever.getUsername()).thenReturn(CONTACTRECIEVERUSERNAME);

        when(pathDeterminationService.getPath(anyInt(), any(Contact.class)))
                .thenReturn(path);
        when(encrypt.encryptData(any(byte[].class), any(byte[].class)))
                .thenReturn("encrypted data".getBytes());
    }

    @Test
    public void buildMessageTest() {

        GeneratedMessage message = makeMessage();
        List<Node> path = new ArrayList<>(this.path.size());
        path.addAll(this.path);

        HanRoutingProtocol.MessageWrapper buildMessage = messageBuilderService
                .buildMessage(message, contactreciever).getMessageWrapper();

        HanRoutingProtocol.Wrapper.Builder wrapperBuilder = HanRoutingProtocol.Wrapper
                .newBuilder();
        wrapperBuilder.setType(HanRoutingProtocol.Wrapper.Type.MESSAGE);

        wrapperBuilder.setData(message.toByteString());

        HanRoutingProtocol.MessageWrapper.Builder endPointMessageWrapper = HanRoutingProtocol.MessageWrapper
                .newBuilder();
        endPointMessageWrapper.setIPaddress(path.get(0).getIpAddress());
        endPointMessageWrapper.setPort(path.get(0).getPort());
        endPointMessageWrapper.setUsername(contactreciever.getUsername());
        endPointMessageWrapper.setData(ByteString.copyFrom(
                encrypt.encryptData(path.get(0).getPublicKey(),
                        wrapperBuilder.build().toByteArray()))); // enc

        for (int i = 1; i < path.size(); i++) {
            HanRoutingProtocol.MessageWrapper.Builder messageWrapperBuilder = HanRoutingProtocol.MessageWrapper
                    .newBuilder();

            messageWrapperBuilder.setIPaddress(path.get(i).getIpAddress());
            messageWrapperBuilder.setPort(path.get(i).getPort());
            messageWrapperBuilder.setData(ByteString.copyFrom(
                    encrypt.encryptData(path.get(i).getPublicKey(),
                            endPointMessageWrapper.build()
                                    .toByteArray()))); // enc

            endPointMessageWrapper = messageWrapperBuilder;
        }

        Assert.assertEquals(buildMessage.getIPaddress(),
                endPointMessageWrapper.build().getIPaddress());
        Assert.assertEquals(buildMessage.getPort(),
                endPointMessageWrapper.build().getPort());
    }

    @Test
    public void buildMessage2hops() {
        path = new ArrayList<>(Arrays.asList(
                new Node("NODE_ID_1-last-node-in-path", "192.168.2.1", 1234,
                        "123456789".getBytes()),
                new Node("NODE_ID_2", "192.168.2.2", 1234,
                        "123456789".getBytes())));

        List<Node> path = new ArrayList<>(this.path.size());
        path.addAll(this.path);
        pathDeterminationService = Mockito.mock(IGetMessagePath.class);

        messageBuilderService = new MessageBuilderService(
                pathDeterminationService, encrypt);

        when(pathDeterminationService.getPath(anyInt(), any(Contact.class)))
                .thenReturn(this.path);

        GeneratedMessage message = makeMessage();

        HanRoutingProtocol.MessageWrapper buildMessage = messageBuilderService
                .buildMessage(message, contactreciever).getMessageWrapper();

        HanRoutingProtocol.Wrapper.Builder wrapperBuilder = HanRoutingProtocol.Wrapper
                .newBuilder();
        wrapperBuilder.setType(HanRoutingProtocol.Wrapper.Type.MESSAGE);

        wrapperBuilder.setData(message.toByteString());

        HanRoutingProtocol.MessageWrapper.Builder endPointMessageWrapper = HanRoutingProtocol.MessageWrapper
                .newBuilder();
        endPointMessageWrapper.setIPaddress(path.get(0).getIpAddress());
        endPointMessageWrapper.setPort(path.get(0).getPort());
        endPointMessageWrapper.setUsername(contactreciever.getUsername());
        endPointMessageWrapper.setData(ByteString.copyFrom(
                encrypt.encryptData(path.get(0).getPublicKey(),
                        wrapperBuilder.build().toByteArray())));

        HanRoutingProtocol.MessageWrapper.Builder secondDeepestLayer = HanRoutingProtocol.MessageWrapper
                .newBuilder();

        secondDeepestLayer.setIPaddress(path.get(1).getIpAddress());
        secondDeepestLayer.setPort(path.get(1).getPort());
        secondDeepestLayer.setData(ByteString.copyFrom(
                encrypt.encryptData(path.get(1).getPublicKey(),
                        endPointMessageWrapper.build().toByteArray())));

        Assert.assertEquals(buildMessage.getIPaddress(),
                secondDeepestLayer.build().getIPaddress());
        Assert.assertEquals(buildMessage.getPort(),
                secondDeepestLayer.build().getPort());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void whenThereIsNoNodesInPathThrowIndexOutOfBoundsException() {
        path = new ArrayList<>();

        pathDeterminationService = Mockito.mock(IGetMessagePath.class);

        messageBuilderService = new MessageBuilderService(
                pathDeterminationService, encrypt);

        when(pathDeterminationService.getPath(anyInt(), any(Contact.class)))
                .thenReturn(path);
        GeneratedMessage message = makeMessage();

        messageBuilderService.buildMessage(message, contactreciever);

    }

    @Test
    public void buildConfirmationMessageTest() {
        path = new ArrayList<>(Arrays.asList(
                new Node("NODE_ID_1-last-node-in-path", "192.168.2.1", 1234,
                        "123456789".getBytes()),
                new Node("NODE_ID_2", "192.168.2.2", 1234,
                        "123456789".getBytes())));

        List<Node> path = new ArrayList<>(this.path.size());
        path.addAll(this.path);

        pathDeterminationService = Mockito.mock(IGetMessagePath.class);

        messageBuilderService = new MessageBuilderService(
                pathDeterminationService, encrypt);

        when(pathDeterminationService.getPath(anyInt(), any(Contact.class)))
                .thenReturn(this.path);

        GeneratedMessage messageConfirmation = makeMessageConfirmation();

        HanRoutingProtocol.MessageWrapper buildMessage = messageBuilderService
                .buildMessage(messageConfirmation, contactreciever)
                .getMessageWrapper();

        HanRoutingProtocol.Wrapper.Builder wrapperBuilder = HanRoutingProtocol.Wrapper
                .newBuilder();
        wrapperBuilder
                .setType(HanRoutingProtocol.Wrapper.Type.MESSAGECONFIRMATION);

        wrapperBuilder.setData(messageConfirmation.toByteString());

        HanRoutingProtocol.MessageWrapper.Builder endPointMessageWrapper = HanRoutingProtocol.MessageWrapper
                .newBuilder();
        endPointMessageWrapper.setIPaddress(path.get(0).getIpAddress());
        endPointMessageWrapper.setPort(path.get(0).getPort());
        endPointMessageWrapper.setUsername(contactreciever.getUsername());
        endPointMessageWrapper.setData(ByteString.copyFrom(
                encrypt.encryptData(path.get(0).getPublicKey(),
                        wrapperBuilder.build().toByteArray())));

        HanRoutingProtocol.MessageWrapper.Builder secondDeepestLayer = HanRoutingProtocol.MessageWrapper
                .newBuilder();

        secondDeepestLayer.setIPaddress(path.get(1).getIpAddress());
        secondDeepestLayer.setPort(path.get(1).getPort());
        secondDeepestLayer.setData(ByteString.copyFrom(
                encrypt.encryptData(path.get(1).getPublicKey(),
                        endPointMessageWrapper.build().toByteArray())));

        HanRoutingProtocol.MessageWrapper secondDeepestLayerBuild = secondDeepestLayer
                .build();

        Assert.assertEquals(buildMessage.getIPaddress(),
                secondDeepestLayerBuild.getIPaddress());
        Assert.assertEquals(buildMessage.getPort(),
                secondDeepestLayerBuild.getPort());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExeptionWhenWrongMessageIsUsed() {
        messageBuilderService.buildMessage(makeWrongMessage(), contactreciever);
    }

    private HanRoutingProtocol.Message makeMessage() {
        HanRoutingProtocol.Message.Builder messageBuilder = HanRoutingProtocol.Message
                .newBuilder();

        messageBuilder.setText("This is a message");
        messageBuilder.setSender("Alice");
        messageBuilder.setTimeSent(Instant.now().getEpochSecond());
        messageBuilder.setId(messageId.toString());

        return messageBuilder.build();
    }

    private HanRoutingProtocol.MessageConfirmation makeMessageConfirmation() {
        HanRoutingProtocol.MessageConfirmation.Builder messageConfirmation = HanRoutingProtocol.MessageConfirmation
                .newBuilder();

        messageConfirmation.setConfirmationId(messageId.toString());

        return messageConfirmation.build();
    }

    private HanRoutingProtocol.MessageWrapper makeWrongMessage() {
        HanRoutingProtocol.MessageWrapper.Builder messageWrapperBuilder = HanRoutingProtocol.MessageWrapper
                .newBuilder();

        messageWrapperBuilder.setData(ByteString.copyFrom("data".getBytes()));
        messageWrapperBuilder.setPort(1337);
        messageWrapperBuilder.setIPaddress("192.168.2.14");

        return messageWrapperBuilder.build();
    }

    @After
    public void tearDown() throws Exception {

    }
}
