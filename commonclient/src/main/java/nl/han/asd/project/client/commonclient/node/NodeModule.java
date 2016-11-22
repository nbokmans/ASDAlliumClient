package nl.han.asd.project.client.commonclient.node;

import com.google.inject.AbstractModule;

public class NodeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IConnectedNodes.class).to(NodeConnectionService.class);
        bind(ISendData.class).to(NodeConnectionService.class);
    }
}
