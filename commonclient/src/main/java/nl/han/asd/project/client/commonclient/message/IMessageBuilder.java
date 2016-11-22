package nl.han.asd.project.client.commonclient.message;

import com.google.protobuf.GeneratedMessage;
import nl.han.asd.project.client.commonclient.store.Contact;

public interface IMessageBuilder {
    <T extends GeneratedMessage> MessagePathBundle buildMessage(T generatedMessage , Contact contactReceiver);
}
