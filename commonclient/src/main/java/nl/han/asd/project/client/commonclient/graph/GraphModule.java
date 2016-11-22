package nl.han.asd.project.client.commonclient.graph;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class GraphModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GraphManagerService.class).in(Singleton.class);
        bind(IGetVertices.class).to(GraphManagerService.class);
        bind(IUpdateGraph.class).to(GraphManagerService.class);
    }
}
