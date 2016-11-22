package nl.han.asd.project.client.commonclient.heartbeat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.master.IHeartbeat;
import nl.han.asd.project.client.commonclient.store.CurrentUser;
import nl.han.asd.project.commonservices.internal.utility.Check;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientHeartbeat;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientHeartbeat.Builder;

/**
 * Runnable implementation used to periodically construct
 * and send a heartbeat message to the master application.
 *
 * @version 1.0
 */
public class ThreadedHeartbeatService implements IHeartbeatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadedHeartbeatService.class);
    private Properties properties;
    private IHeartbeat heartbeat;
    private Map<String, HeartbeatSender> activeHeartbeats;

    /**
     * Construct a new HeartbeatService instance using the provided
     * parameters. This implementation only logs and not
     * terminates on checked exceptions.
     * <p/>
     * <p/>
     * Note that this implementation relies on the properties
     * defined in the properties parameter to find the timing details.
     * <p/>
     * <p/>
     * The following properties are expected:
     * <pre>
     *  properties name                         | definition                            | example
     *  {@link PropertyValues#HEARTBEAT_DELAY}          | heartbeat interval in seconds         | 10
     * </pre>
     * Note that this implementation sends no heartbeats prior to calling
     * HeartbeatService.startHeartbeatFor(User) for the current user.
     * <p/>
     * <p/>
     * This implementation neither defensively copies nor checks the
     * presents of properties during creation. Removing required properties
     * during/after instance creation is considered an error.
     *
     * @param properties retrive heartbeat timing information
     * @param heartbeat  instance used during the heartbeat message sending
     * @throws IllegalArgumentException if properties and/or heartbeat is null
     */
    @Inject
    public ThreadedHeartbeatService(Properties properties, IHeartbeat heartbeat) {
        this.properties = Check.notNull(properties, "properties");
        this.heartbeat = Check.notNull(heartbeat, "heartbeat");

        activeHeartbeats = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p/>
     * Note that this method starts a new thread on every
     * successful call and should be coupled with the
     * HeartbeatService.stopHeartbeatFor(user) method to
     * remove the created threads.
     * <p/>
     * <p/>
     * If a heartbeat is already scheduled for the provided
     * user (determined by matching usernames) no new thread
     * will be started.
     * <p/>
     * <p/>
     * This method does not defensively copy the provided
     * parameter. It is therefore considered an error
     * to change instance variables of the user
     * class before calling stopHeartbeatFor(user).
     */
    @Override
    public void startHeartbeatFor(CurrentUser contact) {
        Check.notNull(contact, "contact");

        if (activeHeartbeats.containsKey(contact.asContact().getUsername())) {
            return;
        }

        Thread heartbeatThread = new HeartbeatSender(contact);
        heartbeatThread.start();

        activeHeartbeats.put(contact.asContact().getUsername(), (HeartbeatSender) heartbeatThread);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p/>
     * This implementation guarantees that no more heartbeats will
     * be send after a successful execution of this method.
     */
    @Override
    public void stopHeartbeatFor(CurrentUser contact) throws InterruptedException {
        Check.notNull(contact, "contact");

        HeartbeatSender heartbeatSender = activeHeartbeats.get(contact.asContact().getUsername());

        if (heartbeatSender == null) {
            return;
        }

        heartbeatSender.isRunning = false;
        heartbeatSender.interrupt();
        heartbeatSender.join();

        LOGGER.trace("heartbeat stopped for [{}]", contact);
    }

    private enum PropertyValues {
        HEARTBEAT_DELAY("heartbeat-delay");

        private String value;

        private PropertyValues(String value) {
            this.value = Check.notNull(value, "value");
        }

        String get(Properties properties) {
            return properties.getProperty(value);
        }

        Integer getInteger(Properties properties) {
            return Integer.valueOf(get(properties));
        }
    }

    public class HeartbeatSender extends Thread {
        private volatile boolean isRunning = true;

        private CurrentUser contact;

        protected HeartbeatSender(CurrentUser contact) {
            this.contact = contact;
        }

        /**
         * Send heartbeat on specified interval
         */
        @Override
        public void run() {
            long delay = TimeUnit.SECONDS.toNanos(PropertyValues.HEARTBEAT_DELAY.getInteger(properties));

            LOGGER.trace("heartbeat started for [{}]", contact);

            while (isRunning) {
                LockSupport.parkNanos(delay);

                if (!isRunning) {
                    return;
                }

                try {
                    heartbeat.sendHeartbeat(buildheartbeat());
                } catch (IOException | MessageNotSentException e) {
                    LOGGER.debug(e.getMessage(), e);
                }
            }
        }

        private ClientHeartbeat buildheartbeat() {
            Builder clientHeartbeatBuilder = ClientHeartbeat.newBuilder();

            clientHeartbeatBuilder.setUsername(contact.asContact().getUsername());
            clientHeartbeatBuilder.setSecretHash(contact.getSecretHash());

            return clientHeartbeatBuilder.build();
        }
    }

}
