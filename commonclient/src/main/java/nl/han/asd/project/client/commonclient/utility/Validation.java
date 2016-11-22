package nl.han.asd.project.client.commonclient.utility;

import static org.apache.http.conn.util.InetAddressUtils.*;

/**
 * Provides validation methods.
 *
 * @version 1.0
 */
public final class Validation {

    private static final String REGEX_ALPHANUMERIC = "[a-zA-Z0-9_-]*";

    /**
     * Private constructor to prevent instantiation.
     */
    private Validation() {
        throw new UnsupportedOperationException("You may not instantiate this class.");
    }

    /**
     * Validates an IP address.
     *
     * @param address The address to check.
     * @return <tt>true</tt> if the address is valid, <tt>false</tt> otherwise.
     */
    public static boolean isValidAddress(String address) {
        return isIPv4Address(address) || isIPv6Address(address) || isIPv6StdAddress(address) || isIPv6HexCompressedAddress(address);
    }

    /**
     * @param port Port must be in range 1024 - 65535
     *             You can create a server on ports 1 through 65535.
     *             Port numbers less than 256 are reserved for well-known services (like HTTP on port 80) and port numbers less than 1024 require root access on UNIX systems.
     *             Specifying a port of 0 in the ServerSocket constructor results in the server listening on a random, unused port, usually greater than 1024.
     *             http://www.jguru.com/faq/view.jsp?EID=17521
     * @return <tt>true</tt> if the port is valid, <tt>false</tt> otherwise.
     */
    public static boolean isValidPort(int port) {
        return port >= 1024 && port <= 65535;
    }

    /**
     * This method calls the validation of the username and the validation of the password.
     * This method returns true if both validation methods return true.
     *
     * @param username the username to validate.
     * @param password the password to validate.
     * @return boolean if validated or not.
     */
    public static boolean validateCredentials(String username, String password) {
        return isValidUsername(username) && isValidPassword(password);
    }

    private static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty() || !username.matches(REGEX_ALPHANUMERIC)) {
            throw new IllegalArgumentException("Invalid username! Username may only consist of digits, numbers, underscores and dashes.");
        }
        if (username.length() < 3 || username.length() > 40) {
            throw new IllegalArgumentException("Invalid username! Username length should be between 3 and 40 characters.");
        }
        return true;
    }

    private static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty() || !password.matches(REGEX_ALPHANUMERIC)) {
            throw new IllegalArgumentException("Invalid password! Password may only consist of digits, numbers, underscores and dashes.");
        }
        if (password.length() < 8 || password.length() > 40) {
            throw new IllegalArgumentException("Invalid password! Password length should be between 8 and 40 characters.");
        }
        return true;
    }

    /**
     * Checks if the passwords are equal
     *
     * @param password       The password to check.
     * @param passwordRepeat The password to check.
     * @return <tt>true</tt> if it's a valid password, <tt>false</tt> otherwise.
     */
    public static boolean passwordsEqual(String password, String passwordRepeat) {
        if (password.equals(passwordRepeat)) {
            return true;
        } else {
            throw new IllegalArgumentException("The passwords are not the same.");
        }
    }
}
