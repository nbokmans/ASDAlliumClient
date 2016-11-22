package nl.han.asd.project.client.commonclient.heartbeat;

import nl.han.asd.project.client.commonclient.store.CurrentUser;

/**
 * Define the heartbeat scheduling methods.
 *
 * @version 1.0
 */
public interface IHeartbeatService {

    /**
     * Schedule heartbeats for a given contact instance.
     *
     * @param contact to schedule the heartbeats for
     * @throws IllegalArgumentException if contact is null
     */
    public void startHeartbeatFor(CurrentUser contact);

    /**
     * De-schedule the heartbeats for a given contact instance.
     *
     * @param contact to remove the scheduled heartbeats for
     * @throws InterruptedException if the current thread was
     *                              interrupted during the joining of the heartbeat thread
     */
    public void stopHeartbeatFor(CurrentUser contact) throws InterruptedException;
}
