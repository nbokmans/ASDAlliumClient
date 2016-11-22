package nl.han.asd.project.client.commonclient.login;

/**
 * Indicate that the specified username password
 * combination is invalid.
 *
 * @version 1.0
 */
public class MisMatchingException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new InvalidCredentialsException
     * with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public MisMatchingException(String cause) {
        super(cause);
    }

}
