package nl.han.asd.project.client.android.contact;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.android.message.MessageActivity_;
import nl.han.asd.project.client.commonclient.store.Contact;

/**
 * Created by Julius on 31/05/16.
 */
@EViewGroup(R.layout.contact_item)
public class ContactItemView extends LinearLayout {
    private Contact contact = null;

    @ViewById
    protected TextView usernameView;

    @ViewById
    protected TextView btDelete;

    @ViewById
    protected ImageView contactLetter;

    @Click
    public void swipe_horizontal_menu(){
        Intent intent = new Intent(getContext(), MessageActivity_.class);
        intent.putExtra("username", contact.getUsername());
        getContext().startActivity(intent);
    }

    public ContactItemView(Context context) {
        super(context);
    }

    public void bind(final Contact contact, View.OnClickListener deleteListener) {
        this.contact = contact;
        String letter = String.valueOf(contact.getUsername().charAt(0)).toUpperCase();
        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, generator.getColor(contact.getUsername()));
        contactLetter.setImageDrawable(drawable);
        usernameView.setText(contact.getUsername());
        btDelete.setOnClickListener(deleteListener);
    }
}