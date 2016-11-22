package nl.han.asd.project.client.android.contact;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import nl.han.asd.project.client.android.commonclient.CommonClient;
import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.android.utility.BaseActivity;
import nl.han.asd.project.client.android.utility.DividerItemDecoration;
import nl.han.asd.project.client.commonclient.message.Message;
import nl.han.asd.project.client.commonclient.store.Contact;

@EActivity(R.layout.activity_contact)
public class ContactActivity extends BaseActivity {
    @ViewById
    protected RecyclerView contact_recycle_view;

    @Bean
    protected ContactAdapter contactAdapter;

    @AfterViews
    public void init(){
        contact_recycle_view.setLayoutManager(new LinearLayoutManager(this));
        contact_recycle_view.addItemDecoration(new DividerItemDecoration(this));
        contact_recycle_view.setAdapter(contactAdapter);
    }

    @Override
    public void afterBind(){
        updateContacts();
    }

    @UiThread
    public void updateContacts(){
        List<Contact> contacts = commonClient.getGateway().getContacts();
        contactAdapter.clear();
        contactAdapter.add(contacts);
    }

    @Click
    public void fab(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View input = inflater.inflate(R.layout.dialog_contact_add, null);
        builder.setView(input);
        builder.setPositiveButton(R.string.add, null);
        builder.setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener(){
            @Override
            public void onShow(DialogInterface d){
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText contact = (EditText)input.findViewById(R.id.contactname);
                        String username = contact.getText().toString();
                        if(username.isEmpty()){
                            contact.setError(getString(R.string.contact_name_empty_error));
                            return;
                        }
                        addContact(username, dialog);
                    }
                });
            }
        });
        dialog.show();
    }

    @Background
    protected void addContact(String username, AlertDialog dialog){
        commonClient.getGateway().addContact(username);
        addContactToRecycleView(username);
        dismissContactDialog(dialog);
    }

    @UiThread
    protected void addContactToRecycleView(String username) {
        Contact contact = new Contact(username);
        contactAdapter.add(contact);
    }

    @UiThread
    protected void dismissContactDialog(AlertDialog dialog){
        dialog.dismiss();
    }

    @Background
    public void deleteContact(Contact contact){
        commonClient.getGateway().removeContact(contact.getUsername());
        removeContactFromRecycleView(contact);
    }

    @UiThread
    protected void removeContactFromRecycleView(Contact contact) {
        contactAdapter.remove(contact);
    }

    @Override
    public void receivedMessage(Message message) {

    }

    @Override
    public void confirmedMessage(String messageId) {

    }
}