package integration.nl.han.asd.project.client.commonclient;

import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.client.commonclient.login.InvalidCredentialsException;
import nl.han.asd.project.protocol.HanRoutingProtocol;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class CommonClientGatewayIT extends ITHelper{

    private String validUsername = "Username";
    private String validPassword = "Pass1234";

    @Before
    public void setup() {
        startMaster();
        injectInterfaces();
    }

    @After
    public void after() {
        master.teardown();
    }

    @Test
    public void testRegisterRequestReturnSuccessMessage() throws Exception {
        Assert.assertEquals(HanRoutingProtocol.ClientRegisterResponse.Status.SUCCES,
                commonClientGateway.registerRequest(validUsername, validPassword, validPassword));
    }

    @Test
    public void testRegisterTakenUsernameReturnError() throws IOException, MessageNotSentException {
        commonClientGateway.registerRequest(validUsername, validPassword, validPassword);
        Assert.assertEquals(HanRoutingProtocol.ClientRegisterResponse.Status.TAKEN_USERNAME,
                commonClientGateway.registerRequest(validUsername, validPassword, validPassword));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTooLongUsernameReturnError() throws IOException, MessageNotSentException {
        String longUsername = "ThisUsernameIsWayTooLongToBeACorrectUsernameForThisApp";
        commonClientGateway.registerRequest(longUsername, validPassword, validPassword);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTooShortUsernameReturnError() throws IOException, MessageNotSentException {
        String shortUsername = "u";
        commonClientGateway.registerRequest(shortUsername, validPassword, validPassword);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTooLongPasswordReturnError() throws IOException, MessageNotSentException {
        String longPassword = "ThisPasswordIsWayTooLongToBeACorrectPasswordForThisApp";
        commonClientGateway.registerRequest(validUsername, longPassword, longPassword);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterInvalidUsernameReturnError() throws IOException, MessageNotSentException {
        String invalidUsername = "User*$)name";
        commonClientGateway.registerRequest(invalidUsername, validPassword, validPassword);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterInvalidPasswordReturnError() throws IOException, MessageNotSentException {
        String invalidPassword = "Pass*%($word";
        commonClientGateway.registerRequest(validUsername, invalidPassword, invalidPassword);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterTooShortPasswordReturnError() throws IOException, MessageNotSentException {
        String shortPassword = "p";
        commonClientGateway.registerRequest(validUsername, shortPassword, shortPassword);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testLoginWithNonExistingAccountReturnError() throws Exception {
        commonClientGateway.loginRequest(validUsername, validPassword);
    }

    @Test
    public void testLoginWithExistingAccountReturnError() throws Exception {
        commonClientGateway.registerRequest(validUsername, validPassword, validPassword);
        Assert.assertEquals(HanRoutingProtocol.ClientLoginResponse.Status.SUCCES,
                commonClientGateway.loginRequest(validUsername, validPassword));
    }
}


