package nl.han.asd.project.client.commonclient.login;

import nl.han.asd.project.commonservices.internal.utility.Check;

/**
 * Define validity checking methods for user
 * details.
 *
 * @version 1.0
 */
public class UserCheck {

    private static final String REGEX_ALPHANUMERIC = "[a-zA-Z0-9]*";

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 12;

    private static final int MAX_PASSWORD_LENGTH = 16;
    private static final int MIN_PASSWORD_LENGTH = 8;

    private UserCheck() {

    }

    /**
     * Check the validity of the username according
     * to a predefined format.
     * <p/>
     * <p/>
     * The rules describing a valid username are as follows:
     * <pre>
     *  LENGTH : {@code > {@value #MIN_USERNAME_LENGTH} && < {@value #MAX_USERNAME_LENGTH}}
     *  CHARACTERS : {@value #REGEX_ALPHANUMERIC}
     * </pre>
     *
     * @param username to be checked username
     * @return the checked username
     * @throws IllegalUsernameException if the usernames
     *                                  format is invalid
     */
    public static String checkUsername(String username) {
        Check.notNull(username, "username");

        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalUsernameException(
                    String.format("Invalid username; length must be between %d and %d. actual: %d,",
                            MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH, username.length()));
        }

        if (!username.matches(REGEX_ALPHANUMERIC)) {
            throw new IllegalUsernameException(
                    String.format("Invalid username; must match regular expression '%s'.", REGEX_ALPHANUMERIC));
        }

        return username;
    }

    /**
     * Check the validity of the password according
     * to a predefined format.
     * <p/>
     * <p/>
     * The rules describing a valid password are as follows:
     * <pre>
     *  LENGTH : {@code > {@value #MIN_PASSWORD_LENGTH} && < {@value #MAX_PASSWORD_LENGTH}}
     * </pre>
     *
     * @param password to be checked password
     * @return the checked password
     * @throws IllegalPasswordException if the password
     *                                  format is invalid
     */
    public static String checkPassword(String password) {
        Check.notNull(password, "password");

        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalPasswordException(
                    String.format("Invalid password; length must be between %d and %d. actual: %d,",
                            MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH, password.length()));
        }

        return password;
    }
}
