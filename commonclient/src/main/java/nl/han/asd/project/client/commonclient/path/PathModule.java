package nl.han.asd.project.client.commonclient.path;

import com.google.inject.AbstractModule;

public class PathModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IGetMessagePath.class).to(PathDeterminationService.class);
    }
}
