package nl.han.asd.project.client.commonclient.path;

import nl.han.asd.project.client.commonclient.graph.Node;
import nl.han.asd.project.client.commonclient.store.Contact;
import java.util.List;

public interface IGetMessagePath {
    List<Node> getPath(int minHops, Contact contactReciever);
}
