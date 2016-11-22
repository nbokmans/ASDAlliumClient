package nl.han.asd.project.client.commonclient.login;

/**
 * Indicate that the checked username does not
 * meet the specifications.
 *
 * @version 1.0
 */
public class IllegalUsernameException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new IllegalUsernameException
     * with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public IllegalUsernameException(String cause) {
        super(cause);
    }
}
