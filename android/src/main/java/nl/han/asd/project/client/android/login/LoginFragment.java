package nl.han.asd.project.client.android.login;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.android.contact.ContactActivity_;
import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.login.IllegalPasswordException;
import nl.han.asd.project.client.commonclient.login.IllegalUsernameException;
import nl.han.asd.project.client.commonclient.login.InvalidCredentialsException;

@EFragment(R.layout.activity_login)
public class LoginFragment extends Fragment {
    @ViewById
    public EditText username_input;
    @ViewById
    public EditText password_input;

    private BootActivity_ activity;

    @AfterViews
    public void init(){
        activity = (BootActivity_) getActivity();
    }

    @Click
    public void login_button() {
        String username = username_input.getText().toString();
        String password = password_input.getText().toString();
        login(username, password);
    }

    @Click
    public void register_button() {
        ((BootActivity) getActivity()).switchPage(BootActivity.FRAGMENT_REGISTER);
    }

    @Background
    public void login(String username, String password) {
        try {
            activity.commonClient.getGateway().loginRequest(username, password);
        } catch (InvalidCredentialsException | IllegalPasswordException | IllegalUsernameException e) {
            setPasswordError(getString(R.string.login_username_password_invalid_msg));
            return;
        } catch (Exception e) {
            showToast(getString(R.string.login_exception_message) + "\nmessage: " + e.getMessage());
            return;
        }
        handleSuccessfulLoginResponse();
    }

    @UiThread
    protected void handleSuccessfulLoginResponse() {
        Intent intent = new Intent(getActivity(), ContactActivity_.class);
        startActivity(intent);
    }

    @UiThread
    protected void setPasswordError(String error) {
        password_input.setError(error);
    }

    @UiThread
    protected void showToast(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

}
