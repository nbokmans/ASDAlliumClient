package nl.han.asd.project.client.android.message;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.android.utility.BaseActivity;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.store.Contact;

@EActivity(R.layout.activity_message)
public class MessageActivity extends BaseActivity {
    @ViewById
    RecyclerView message_recycle_view;

    @ViewById
    EditText message_text_input;

    @ViewById
    TextView messageTextView;

    @Bean
    MessageAdapter messageAdapter;

    @Extra
    String username;

    Contact currentUserAsContact;

    @AfterViews
    public void init() {
        initMessagesView();
    }

    @Override
    public void afterBind() {
        currentUserAsContact = commonClient.getGateway().getCurrentUser().asContact();
        setContact(commonClient.getGateway().findContact(username));
        resubscribeReceivedMessages(username);
        updateMessages();
        initActionBar();
    }

    @UiThread
    protected void updateMessages() {
        List<Message> messages = commonClient.getGateway().getMessagesFromUser(username);
        if(messages == null){
            return;
        }
        Collections.reverse(messages);
        messageAdapter.clear();
        messageAdapter.add(messages);
    }

    private void initActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getContact().getUsername());
        }
    }

    private void initMessagesView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        message_recycle_view.setLayoutManager(layoutManager);
        message_recycle_view.setAdapter(messageAdapter);
    }

    @Click
    public void send_button() {
        String messageText = message_text_input.getText().toString();
        if (messageText.length() > 0) {
            Message message = new Message(currentUserAsContact, getContact(), new Date(), messageText, "");
            sendMessage(message);
        }
        message_text_input.setText("");
        message_recycle_view.scrollToPosition(0);
    }

    @Background
    protected void sendMessage(Message message) {
        try {
            commonClient.getGateway().sendMessage(message);
        }
        catch(IndexOutOfBoundsException e){
            Log.d(TAG, "User is offline");
        }
        addMessage(message);
    }

    @UiThread
    protected void addMessage(Message message) {
        messageAdapter.add(message, 0);
    }

    @Override
    public void receivedMessage(Message message) {
        if (!messageAdapter.contains(message)) {
            addMessage(message);
        }
    }

    @Override
    public void confirmedMessage(String messageId) {
        Message message = commonClient.getGateway().getMessage(messageId);
        messageAdapter.confirmMessage(message);
    }
}
