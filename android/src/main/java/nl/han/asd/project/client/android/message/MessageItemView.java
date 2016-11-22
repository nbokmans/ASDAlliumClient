package nl.han.asd.project.client.android.message;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;

import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.commonclient.message.Message;

@EViewGroup(R.layout.message_item)

public class MessageItemView extends LinearLayout {
    Message message;

    @ViewById
    LinearLayout message_view;

    @ViewById
    TextView messageTextView;

    @ViewById
    ImageView messageConfirmImage;

    @ViewById
    TextView messageTime;

    public MessageItemView(Context context) {
        super(context);
    }

    public void bind(Message message, View.OnClickListener openInfoListener) {
        this.message = message;
        messageTextView.setText(message.getText());
        messageTime.setText(formatDate(message.getMessageTimestamp()));

        MessageActivity_ activity = (MessageActivity_) getContext();
        if (message.getSender().equals(activity.currentUserAsContact)) {
            messageTextView.setOnClickListener(openInfoListener);
            applyOwnMessageStyle(message_view);
        } else {
            applyOtherMessageStyle(message_view);
        }
        if(message.isConfirmed()){
            showConfirmImage();
        }
        else{
            hideConfirmImage();
        }
    }

    private String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return String.format("%02d:%02d", hours, minutes);
    }

    private void applyOtherMessageStyle(View view) {
        setLayoutGravity(view, Gravity.START);
        view.setBackgroundResource(R.drawable.message_other_background);
    }

    private void applyOwnMessageStyle(View view) {
        setLayoutGravity(view, Gravity.END);
        view.setBackgroundResource(R.drawable.message_own_background);
    }

    private void setLayoutGravity(View view, int gravity){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;

        view.setLayoutParams(params);
    }

    public void showConfirmImage(){
        messageConfirmImage.setVisibility(VISIBLE);
    }

    private void hideConfirmImage() {
        messageConfirmImage.setVisibility(INVISIBLE);
    }

}
