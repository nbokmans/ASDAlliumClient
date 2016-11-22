package nl.han.asd.project.client.commonclient.message;

import com.google.protobuf.GeneratedMessage;

public interface IReceiveMessage {
    /**
     * Process an incoming message.
     * The message can either be a Message or a MessageConfirmation. Both are handled differently
     *
     * @param messageWrapper Wrapper containing the received message
     */
    void processIncomingMessage(GeneratedMessage messageWrapper);
}
