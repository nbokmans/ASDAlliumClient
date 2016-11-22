package nl.han.asd.project.client.commonclient.store;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public class StoreModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IScriptStore.class).to(ScriptStore.class);
        bind(IContactStore.class).to(ContactStore.class).in(Singleton.class);
        bind(IMessageStore.class).to(MessageStore.class);
        bind(IContactManager.class).to(ContactManager.class);
    }

}
