package nl.han.asd.project.client.commonclient.heartbeat;

import com.google.inject.AbstractModule;

/**
 * Bind the heartbeat implementation to the
 * interface.
 *
 * @version 1.0
 */
public class HeartbeatModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IHeartbeatService.class).to(ThreadedHeartbeatService.class);
    }
}

