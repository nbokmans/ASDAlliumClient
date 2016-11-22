package nl.han.asd.project.client.android.login;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import nl.han.asd.project.client.android.R;
import nl.han.asd.project.protocol.HanRoutingProtocol;

/**
 * @author Niels Bokmans
 * @version 1.0
 * @since 3-6-2016
 */
@EFragment(R.layout.activity_register)
public class RegisterFragment extends Fragment {
    @ViewById
    public EditText inpUsername;

    @ViewById
    public EditText inpPassword;

    @ViewById
    public EditText inpPasswordRepeat;

    @ViewById
    public TextView txtErrorMessage;

    private BootActivity_ activity;

    @AfterViews
    public void init(){
        activity = (BootActivity_) getActivity();
    }

    @Click
    public void btnRegister() {
        final String username = inpUsername.getText().toString();
        final String password = inpPassword.getText().toString();
        final String passwordRepeat = inpPasswordRepeat.getText().toString();
        register(username, password, passwordRepeat);
    }

    @Click
    public void btnToLogin() {
        switchToLoginPage();
    }

    @Background
    public void register(String username, String password, String passwordRepeat) {
        try {
            HanRoutingProtocol.ClientRegisterResponse.Status status = activity.commonClient.getGateway().registerRequest(username, password, passwordRepeat);
            switch (status) {
                case SUCCES:
                    showToast(getString(R.string.successfulRegistration));
                    switchToLoginPage();
                    break;
                case TAKEN_USERNAME:
                    showErrorInEditText(getString(R.string.takenUsernameRegistration));
                    break;
                case FAILED:
                    showToast(getString(R.string.failedRegistration));
                    break;
                default:
                    throw new RuntimeException("Shouldn't be here status is " + status.toString());
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                showErrorInEditText(e.getMessage());
            }
        }
    }

    @UiThread
    public void showToast(final String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    public void showErrorInEditText(final String errorMessage) {
        if (errorMessage.contains(getString(R.string.errorRegistrationPasswordRepeat))) {
            inpPasswordRepeat.setError(errorMessage);
        } else if (errorMessage.contains(getString(R.string.errorRegistrationInvalidUsername))) {
            inpUsername.setError(errorMessage);
        } else if (errorMessage.contains(getString(R.string.errorRegistrationInvalidPassword)) && !errorMessage.contains(getString(R.string.errorRegistrationPasswordRepeat))) {
            inpPassword.setError(errorMessage);
        } else if (errorMessage.equalsIgnoreCase(getString(R.string.takenUsername))) {
            inpUsername.setError(getString(R.string.errorRegistrationUsernameTaken));
        } else{
            showToast(errorMessage);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_register, container, false);
    }

    @UiThread
    public void switchToLoginPage() {
        ((BootActivity) getActivity()).switchPage(BootActivity.FRAGMENT_LOGIN);
    }
}
