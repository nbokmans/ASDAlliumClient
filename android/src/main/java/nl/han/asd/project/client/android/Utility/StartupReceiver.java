package nl.han.asd.project.client.android.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EReceiver;

import nl.han.asd.project.client.android.commonclient.CommonClientService_;


@EReceiver
public class StartupReceiver extends BroadcastReceiver {
    public StartupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent msgIntent = new Intent(context, CommonClientService_.class);
        context.startService(msgIntent);
    }
}
