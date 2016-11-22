package nl.han.asd.project.client.commonclient;

import com.google.inject.AbstractModule;

import nl.han.asd.project.client.commonclient.connection.ConnectionModule;
import nl.han.asd.project.client.commonclient.database.DatabaseModule;
import nl.han.asd.project.client.commonclient.graph.GraphModule;
import nl.han.asd.project.client.commonclient.heartbeat.HeartbeatModule;
import nl.han.asd.project.client.commonclient.login.LoginModule;
import nl.han.asd.project.client.commonclient.master.MasterModule;
import nl.han.asd.project.client.commonclient.message.MessageModule;
import nl.han.asd.project.client.commonclient.node.NodeModule;
import nl.han.asd.project.client.commonclient.path.PathModule;
import nl.han.asd.project.client.commonclient.persistence.PersistenceModule;
import nl.han.asd.project.client.commonclient.store.StoreModule;
import nl.han.asd.project.commonservices.encryption.EncryptionModule;

public class CommonClientModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ConnectionModule());
        install(new DatabaseModule());
        install(new GraphModule());
        install(new HeartbeatModule());
        install(new LoginModule());
        install(new MasterModule());
        install(new MessageModule());
        install(new NodeModule());
        install(new PathModule());
        install(new PersistenceModule());
        install(new StoreModule());
        install(new EncryptionModule());
        install(new NodeModule());
    }
}
