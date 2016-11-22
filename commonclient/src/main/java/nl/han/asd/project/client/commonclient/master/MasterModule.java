package nl.han.asd.project.client.commonclient.master;

import com.google.inject.AbstractModule;

/**
 * Bind the mastergateway interfaces to the
 * implementations.
 *
 * @version 1.0
 */
public class MasterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IRegistration.class).to(MasterGateway.class);
        bind(IHeartbeat.class).to(MasterGateway.class);
        bind(IAuthentication.class).to(MasterGateway.class);
        bind(IGetClientGroup.class).to(MasterGateway.class);
        bind(IGetGraphUpdates.class).to(MasterGateway.class);
        bind(ILogout.class).to(MasterGateway.class);
    }
}
