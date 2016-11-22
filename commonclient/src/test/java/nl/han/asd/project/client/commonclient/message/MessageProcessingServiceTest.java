package nl.han.asd.project.client.commonclient.message;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import nl.han.asd.project.client.commonclient.graph.IUpdateGraph;
import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.node.ISendData;
import nl.han.asd.project.client.commonclient.store.*;
import nl.han.asd.project.commonservices.encryption.EncryptionModule;
import nl.han.asd.project.commonservices.encryption.IEncryptionService;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class MessageProcessingServiceTest {

    @Mock private IMessageStore messageStore;
    @Mock private ISendData nodeConnectionService;
    @Mock private IMessageConfirmation messageConfirmationService;
    @Mock private IContactStore contactStore;
    @Mock private IMessageBuilder messageBuilder;
    @Mock private IUpdateGraph updateGraph;
    @Mock private IContactManager contactManager;

    private IEncryptionService encryptionService;

    private MessageProcessingService messageProcessingService;

    @Before
    public void initService() {
        final Injector injector = Guice
                .createInjector(new EncryptionModule(), new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(IMessageStore.class).toInstance(messageStore);
                        bind(ISendData.class).toInstance(nodeConnectionService);
                        bind(IMessageConfirmation.class)
                                .toInstance(messageConfirmationService);
                        bind(IContactStore.class).toInstance(contactStore);
                        bind(IMessageBuilder.class).toInstance(messageBuilder);
                        bind(IUpdateGraph.class).toInstance(updateGraph);
                        bind(IContactManager.class).toInstance(contactManager);
                    }
                });

        encryptionService = injector.getInstance(IEncryptionService.class);
        messageProcessingService = injector
                .getInstance(MessageProcessingService.class);
    }

    @Test
    public void testWithMessageWrapper() {
        HanRoutingProtocol.Message message = getMessage();

        CurrentUser currentUser = mock(CurrentUser.class);
        Contact contact = new Contact("receiver");

        MessagePathBundle messagePathBundle = mock(MessagePathBundle.class);

        when(contactStore.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.asContact()).thenReturn(contact);
        when(messageBuilder
                .buildMessage(any(GeneratedMessage.class), any(Contact.class)))
                .thenReturn(messagePathBundle);

        when(messageBuilder.buildMessage(any(GeneratedMessage.class), any(Contact.class))).thenReturn(messagePathBundle);
        when(messagePathBundle.getMessageWrapper()).thenReturn(null);

        messageProcessingService.processIncomingMessage(message);

        // verify that the addMessage is called (thus no exceptions where thrown)
        verify(messageStore, times(1)).addMessage(any(Message.class));
    }

    @Test
    public void testWithMessageConfirmationWrapper() {
        String confirmedId = "1111111";
        HanRoutingProtocol.MessageConfirmation messageConfirmation = HanRoutingProtocol.MessageConfirmation
                .newBuilder().setConfirmationId(confirmedId).build();

        messageProcessingService.processIncomingMessage(messageConfirmation);

        verify(messageConfirmationService)
                .messageConfirmationReceived(eq(confirmedId));
    }

    @Test
    public void testInvalidType() {
        HanRoutingProtocol.GraphUpdateRequest.Builder graphUpdateRequestBuilder = HanRoutingProtocol.GraphUpdateRequest
                .newBuilder();
        graphUpdateRequestBuilder.setCurrentVersion(1);

        // build wrapper containing Type.GRAPHUPDATEREQUEST, processIncomingMessage should be unable to parse this.
        ByteString wrapperByteString = HanRoutingProtocol.Wrapper.newBuilder()
                .setData(graphUpdateRequestBuilder.build().toByteString())
                .setType(HanRoutingProtocol.Wrapper.Type.GRAPHUPDATEREQUEST)
                .build().toByteString();

        HanRoutingProtocol.MessageWrapper messageWrapper = getMessageWrapper(
                wrapperByteString);
        messageProcessingService.processIncomingMessage(messageWrapper);

        // verify that the addMessage or messageReceived method are never called.
        verify(messageStore, times(0)).addMessage(any(Message.class));
    }

    @Test
    public void testInvalidTypeWithValidTypeParameter() {
        HanRoutingProtocol.GraphUpdateRequest.Builder graphUpdateRequestBuilder = HanRoutingProtocol.GraphUpdateRequest
                .newBuilder();
        graphUpdateRequestBuilder.setCurrentVersion(1);

        // same as testInvalidType, but now we're not setting the type parameter to GraphUpdateRequest but to Message
        ByteString wrapperByteString = HanRoutingProtocol.Wrapper.newBuilder()
                .setData(graphUpdateRequestBuilder.build().toByteString())
                .setType(HanRoutingProtocol.Wrapper.Type.MESSAGE).build()
                .toByteString();

        HanRoutingProtocol.MessageWrapper messageWrapper = getMessageWrapper(
                wrapperByteString);

        // try to process the UnpackedMessage with the MessageWrapper type but message object
        messageProcessingService.processIncomingMessage(messageWrapper);

        // verify that the addMessage or messageReceived method are never called.
        verify(messageStore, times(0)).addMessage(any(Message.class));
    }

    private HanRoutingProtocol.MessageWrapper getMessageWrapper(
            final ByteString message) {
        HanRoutingProtocol.MessageWrapper.Builder messageWrapperBuilder = HanRoutingProtocol.MessageWrapper
                .newBuilder();
        messageWrapperBuilder.setPort(1111);
        messageWrapperBuilder.setIPaddress("127.0.0.1");
        messageWrapperBuilder.setUsername("Alice");

        // encrypt it with our public key, thus only we can decrypt it (must be same instance, see initSerivce).
        ByteString encryptedData = ByteString.copyFrom(encryptionService
                .encryptData(encryptionService.getPublicKey(),
                        message.toByteArray()));

        messageWrapperBuilder.setData(encryptedData);

        return messageWrapperBuilder.build();
    }

    private HanRoutingProtocol.Message getMessage() {
        HanRoutingProtocol.Message.Builder messageBuilder = HanRoutingProtocol.Message
                .newBuilder();
        messageBuilder.setId("3333333");
        messageBuilder.setSender("Charlie");
        messageBuilder.setText("Hey Bob!");
        messageBuilder.setTimeSent(33333333);

        return messageBuilder.build();
    }

    @Test
    public void testMessagePathSaved() {
        Contact contactMock = mock(Contact.class);
        MessagePathBundle messagePathBundleMock = mock(MessagePathBundle.class);
        Message messageMock = Message
                .fromProtocolMessage(getMessage(), contactMock);
        CurrentUser currentUserMock = mock(CurrentUser.class);

        when(contactMock.getUsername()).thenReturn("Alice");
        when(contactStore.findContact(contactMock.getUsername()))
                .thenReturn(contactMock);

        when(contactStore.getCurrentUser()).thenReturn(currentUserMock);
        when(currentUserMock.asContact()).thenReturn(contactMock);

        when(messageBuilder
                .buildMessage(any(GeneratedMessage.class), eq(contactMock)))
                .thenReturn(messagePathBundleMock);

        List<Node> path = Arrays.asList(new Node[] {});
        when(messagePathBundleMock.getPath()).thenReturn(path);

        messageProcessingService.sendMessage(messageMock, contactMock);

        verify(messageStore, times(1))
                .addMessageWithPath(eq(messageMock), eq(path));
    }

}
