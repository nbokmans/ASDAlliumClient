package nl.han.asd.project.client.android.contact;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tubb.smrv.SwipeHorizontalMenuLayout;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.android.utility.RecyclerViewAdapterBase;
import nl.han.asd.project.client.android.utility.ViewWrapper;
import nl.han.asd.project.client.commonclient.store.Contact;

@EBean
public class ContactAdapter extends RecyclerViewAdapterBase<Contact, ContactItemView> {

    @RootContext
    protected Context context;

    @Override
    protected ContactItemView onCreateItemView(ViewGroup parent, int viewType) {
        return ContactItemView_.build(context);
    }

    @Override
    public void onBindViewHolder(ViewWrapper<ContactItemView> viewHolder, int position) {
        final ContactItemView view = viewHolder.getView();
        final Contact person = items.get(position);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewHolder.getView().setLayoutParams(lp);
        view.bind(person, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SwipeHorizontalMenuLayout swipe_horizontal_menu = (SwipeHorizontalMenuLayout) view.findViewById(R.id.swipe_horizontal_menu);
                swipe_horizontal_menu.smoothCloseMenu();
                ((ContactActivity) view.getContext()).deleteContact(person);
            }
        });
    }
}