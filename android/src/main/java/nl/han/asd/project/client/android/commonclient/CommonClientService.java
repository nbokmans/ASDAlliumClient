package nl.han.asd.project.client.android.commonclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.UiThread;
import android.support.v4.app.NotificationCompat;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;
import java.util.List;

import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.android.message.MessageActivity_;
import nl.han.asd.project.client.commonclient.message.IMessageReceiver;
import nl.han.asd.project.client.commonclient.message.Message;

@EService
public class CommonClientService extends Service implements IMessageReceiver {
    public static boolean IS_STARTED = false;
    private final IBinder mBinder = new LocalBinder();
    public List<Subscriber> subscribers;
    @Bean
    CommonClient commonClient;
    @SystemService
    NotificationManager notificationManager;

    public CommonClientService() {
        subscribers = new ArrayList<>();
    }

    public CommonClient getCommonClient() {
        return commonClient;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        IS_STARTED = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerForMessages();
        return START_STICKY;
    }

    @Background
    protected void registerForMessages() {
        commonClient.getGateway().subscribeReceivedMessages(this);
        IS_STARTED = true;
    }

    @UiThread
    public void showNotification(int id, Notification notification) {
        notificationManager.notify(id, notification);
    }

    public void receivedMessage(Message message) {
        notifySubscribersMessageReceived(message);
    }

    private void notifySubscribersMessageReceived(Message message) {
        boolean showNotification = true;
        for (Subscriber sub : subscribers) {
            if (message.getSender().getUsername().equals(sub.username)) {
                sub.subscriber.receivedMessage(message);
                showNotification = false;
            } else if (sub.username.isEmpty()) {
                sub.subscriber.receivedMessage(message);
            }
        }
        if(showNotification){
            Notification notification = buildNotification(message);
            showNotification(message.getDatabaseId(), notification);
        }
    }

    private void notifySubscribersMessageConfirmed(String messageId) {
        for (Subscriber sub : subscribers) {
            sub.subscriber.confirmedMessage(messageId);
        }
    }

    private Notification buildNotification(Message message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_onion_notification_512)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(message.getSender().getUsername())
                .setContentText(message.getText());

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MessageActivity_.class);
        resultIntent.putExtra("username", message.getReceiver().getUsername());
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MessageActivity_.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder.build();
    }

    @Override
    public void confirmedMessage(String messageId) {
        notifySubscribersMessageConfirmed(messageId);
    }

    public void subscribeReceivedMessages(IMessageReceiver receiver, String username) {
        for (Subscriber sub : subscribers) {
            if (sub.username.equals(username)) {
                return;
            }
        }
        subscribers.add(new Subscriber(receiver, username));
    }

    public void unsubscribeReceivedMessages(IMessageReceiver receiver, String username) {
        Subscriber rmSub = null;
        for (Subscriber sub : subscribers) {
            if (sub.username.equals(username) && sub.subscriber == receiver) {
                rmSub = sub;
            }
        }
        if (rmSub != null) {
            subscribers.remove(rmSub);
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public CommonClientService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CommonClientService.this;
        }
    }

    private class Subscriber {
        public IMessageReceiver subscriber;
        public String username;
        public Subscriber(IMessageReceiver subscriber, String username) {
            this.subscriber = subscriber;
            this.username = username;
        }
    }
}
