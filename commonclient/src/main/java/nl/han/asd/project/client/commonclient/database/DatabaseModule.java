package nl.han.asd.project.client.commonclient.database;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

/**
 * Bind the database implementation to the interfaces.
 *
 * @version 1.0
 */
public class DatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IDatabase.class).to(HyperSQLDatabase.class).in(Singleton.class);
    }

}
