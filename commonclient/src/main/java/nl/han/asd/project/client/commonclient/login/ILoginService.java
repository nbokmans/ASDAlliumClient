package nl.han.asd.project.client.commonclient.login;

import nl.han.asd.project.client.commonclient.connection.MessageNotSentException;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLogoutResponse;
import nl.han.asd.project.protocol.HanRoutingProtocol.ClientLoginResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Define the login related methods.
 *
 * @version 1.0
 */
public interface ILoginService {
    /**
     * Check the provided user information and
     * send the provided details to the master server for
     * authentication.
     *
     * @param username identifying name of the user
     * @param password password
     * @throws IllegalArgumentException    if username and/or password
     *                                     is null
     * @throws IllegalUsernameException    if the username is
     *                                     invalid according to the format specified in
     *                                     {@link UserCheck#checkUsername(String)}
     * @throws IllegalPasswordException    if the password
     *                                     is invalid according to the format specified
     *                                     in {@link UserCheck#checkPassword(String)}
     * @throws InvalidCredentialsException if the username - password
     *                                     combination is invalid
     * @throws IOException                 if the function was unable to send
     *                                     the wrapper due to a socket related
     *                                     exception
     * @throws MessageNotSentException     if the connection service
     *                                     was unable to send the message. Note that
     *                                     this exception is not thrown on Socket related
     *                                     exceptions. See IOException.
     */
    ClientLoginResponse.Status login(String username, String password)
            throws InvalidCredentialsException, IOException, MessageNotSentException,
            SQLException;

    /**
     * Check the provided user information and
     * send the provided details to the master server logging out
     *
     * @param username identifying name of the current user
     * @param secretHash secrethash of the current user
     * @throws IllegalArgumentException    if username and/or secrethash
     *                                     is null
     * @throws IllegalUsernameException    if the username is
     *                                     invalid according to the format specified in
     *                                     {@link UserCheck#checkUsername(String)}
     * @throws MisMatchingException        if the username - secrethash
     *                                     combination is invalid
     * @throws IOException                 if the function was unable to send
     *                                     the wrapper due to a socket related
     *                                     exception
     * @throws MessageNotSentException     if the connection service
     *                                     was unable to send the message. Note that
     *                                     this exception is not thrown on Socket related
     *                                     exceptions. See IOException.
     */
    ClientLogoutResponse.Status logout(String username, String secretHash)
            throws IOException, MessageNotSentException, MisMatchingException;
}
