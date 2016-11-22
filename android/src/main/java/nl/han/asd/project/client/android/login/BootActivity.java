package nl.han.asd.project.client.android.login;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.android.commonclient.CommonClientService_;
import nl.han.asd.project.client.android.utility.BaseActivity;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.database.HyperSQLDatabase;
import nl.han.asd.project.client.commonclient.login.MisMatchingException;

@EActivity(R.layout.activity_boot)
public class BootActivity extends BaseActivity {

    public static final int FRAGMENT_LOGIN = 0;
    public static final int FRAGMENT_REGISTER = 1;

    @ViewById
    public ViewPager pager;
    private BootActivityPagerAdapter adapter;

    @AfterViews
    public void init() {
        if (!CommonClientService_.IS_STARTED) {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
                HyperSQLDatabase.databasePathPrepend = getFilesDir().getAbsolutePath() + "/";
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, CommonClientService_.class);
            startService(intent);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public void afterBind() {
        adapter = new BootActivityPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        logout();
    }

    @Background
    protected void logout() {
        if (commonClient.getGateway().getCurrentUser() == null) {
            return;
        }
        try {
            commonClient.getGateway().logout();
        } catch (MessageNotSentException | IOException | MisMatchingException e) {
            Log.e(TAG, "Logout failed");
        }
    }

    public void switchPage(int page) {
        pager.setCurrentItem(page);
    }
}