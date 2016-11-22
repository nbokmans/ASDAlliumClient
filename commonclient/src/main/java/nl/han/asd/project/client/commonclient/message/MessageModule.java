package nl.han.asd.project.client.commonclient.message;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public class MessageModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ISendMessage.class).to(MessageProcessingService.class);
        bind(IReceiveMessage.class).to(MessageProcessingService.class);
        bind(IMessageConfirmation.class).to(MessageConfirmationService.class).in(Singleton.class);
        bind(IMessageBuilder.class).to(MessageBuilderService.class);
        bind(ISubscribeMessageReceiver.class).to(MessageProcessingService.class);
    }
}
