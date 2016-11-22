package nl.han.asd.project.client.commonclient.login;

import com.google.inject.AbstractModule;

/**
 * Bind the login implementations to the
 * interfaces.
 *
 * @version 1.0
 */
public class LoginModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ILoginService.class).to(LoginService.class);
    }
}
