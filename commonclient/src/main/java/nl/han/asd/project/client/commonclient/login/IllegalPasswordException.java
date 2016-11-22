package nl.han.asd.project.client.commonclient.login;

/**
 * Indicate that the checked password does not
 * meet the specifications.
 *
 * @version 1.0
 */
public class IllegalPasswordException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new IllegalPasswordException
     * with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public IllegalPasswordException(String cause) {
        super(cause);
    }

}
