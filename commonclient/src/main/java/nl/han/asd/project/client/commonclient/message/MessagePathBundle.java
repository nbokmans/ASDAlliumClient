package nl.han.asd.project.client.commonclient.message;

import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.protocol.HanRoutingProtocol;

import java.util.List;

/**
 * Holds a MessageWrapper and a Path that is used to send the MessageWrapper to its receiver.
 */
public class MessagePathBundle {
    private HanRoutingProtocol.MessageWrapper messageWrapper;
    private List<Node> path;

    public MessagePathBundle() {
    }

    public MessagePathBundle(HanRoutingProtocol.MessageWrapper message,
            List<Node> path) {
        this.messageWrapper = message;
        this.path = path;
    }

    public HanRoutingProtocol.MessageWrapper getMessageWrapper() {
        return messageWrapper;
    }

    public void setMessageWrapper(HanRoutingProtocol.MessageWrapper message) {
        this.messageWrapper = message;
    }

    public List<Node> getPath() {
        return path;
    }

    public void setPath(List<Node> path) {
        this.path = path;
    }
}
