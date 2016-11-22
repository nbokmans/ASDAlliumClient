package nl.han.asd.project.client.commonclient.persistence;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public class PersistenceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IPersistence.class).to(PersistenceService.class).in(Singleton.class);
    }

}
