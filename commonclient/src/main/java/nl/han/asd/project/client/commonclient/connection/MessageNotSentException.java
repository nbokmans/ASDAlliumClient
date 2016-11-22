package nl.han.asd.project.client.commonclient.connection;

/**
 * Thrown by the {@link ConnectionService} if
 * the instance was unable to send the message.
 * Note that this exception occurs outside of
 * socket related exception.
 *
 * @version 1.0
 */
public class MessageNotSentException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a MessageNotSentException
     * with the specified Throwable.
     *
     * @param throwable the exception that was thrown
     */
    public MessageNotSentException(Throwable throwable) {
        super(throwable);
    }
}
