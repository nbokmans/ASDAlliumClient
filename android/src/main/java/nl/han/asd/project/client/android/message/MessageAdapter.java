package nl.han.asd.project.client.android.message;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import nl.han.asd.project.client.android.utility.RecyclerViewAdapterBase;
import nl.han.asd.project.client.android.utility.ViewWrapper;
import nl.han.asd.project.client.commonclient.message.Message;

/**
 * Created by Julius on 01/06/16.
 */
@EBean
public class MessageAdapter extends RecyclerViewAdapterBase<Message, MessageItemView> {
    @RootContext
    Context context;

    @Override
    protected MessageItemView onCreateItemView(ViewGroup parent, int viewType) {
        return MessageItemView_.build(context);

    }

    @Override
    public void onBindViewHolder(ViewWrapper<MessageItemView> viewHolder, int position) {
        MessageItemView view = viewHolder.getView();
        final Message message = items.get(position);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewHolder.getView().setLayoutParams(lp);
        final MessageActivity_ activity = (MessageActivity_) context;

        view.bind(message, new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context,MessageDetailsActivity_.class);
                intent.putExtra("MESSAGE_TEXT",message.getText());
                List<String> path = activity.commonClient.getGateway().getPathByMessageId(message.getMessageId());
                intent.putStringArrayListExtra("MESSAGE_PATH", new ArrayList<>(path));
                context.startActivity(intent);
            }
        });
    }

    @UiThread
    protected void messageChanged(int index, Message message){
        items.set(index, message);
        notifyItemChanged(index, message);
    }

    public void confirmMessage(Message message) {
        int messageIndex = items.indexOf(message);
        if(messageIndex != -1) {
            messageChanged(messageIndex, message);
        }
    }
}
