package nl.han.asd.project.client.android.utility;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;

import nl.han.asd.project.client.android.commonclient.CommonClient;
import nl.han.asd.project.client.android.commonclient.CommonClientService;
import nl.han.asd.project.client.android.commonclient.CommonClientService_;
import nl.han.asd.project.client.commonclient.message.IMessageReceiver;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.store.Contact;

/**
 * Created by rik on 4-6-2016.
 */
@EActivity
public abstract class BaseActivity extends AppCompatActivity implements IMessageReceiver{
    protected String TAG;

    public static boolean Running = false;
    protected CommonClientService mService;
    protected boolean mBound;
    private Contact contact = null;
    public CommonClient commonClient;

    public void setContact(Contact contact){
        this.contact = contact;
    }

    public Contact getContact(){
        return contact;
    }

    @AfterInject
    protected void preInit()
    {
        TAG = this.getClass().getSimpleName();
        bindWithMessageService();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    protected ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CommonClientService.LocalBinder binder = (CommonClientService.LocalBinder) service;
            mService = binder.getService();
            serviceBound();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceUnbound();
            mBound = false;
        }
    };

    private void serviceBound(){
        if(mService != null) {
            String username = "";
            mService.subscribeReceivedMessages(this, username);
            commonClient = mService.getCommonClient();
            afterBind();
        }
    }

    private void serviceUnbound(){
        if(mService != null) {
            String username = "";
            mService.unsubscribeReceivedMessages(this, username);
            commonClient = null;
        }
    }

    @Background
    public void resubscribeReceivedMessages(String username){
        if(mService != null){
            mService.unsubscribeReceivedMessages(this, "");
            mService.subscribeReceivedMessages(this, username);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        bindWithMessageService();
        Running = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        unbindFromService();
        Running = false;
    }

    @Override
    public void onPause(){
        super.onPause();
        unbindFromService();
        Running = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!mBound)
            bindWithMessageService();
        Running = true;
    }

    private void unbindFromService() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Background
    protected void bindWithMessageService(){
        Intent intent = new Intent(this, CommonClientService_.class);
        if(!mBound) {
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void receivedMessage(Message var1){

    };

    @Override
    public void confirmedMessage(String var1){

    };

    public void afterBind(){

    }
}
