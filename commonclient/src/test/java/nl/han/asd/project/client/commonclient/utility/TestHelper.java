package nl.han.asd.project.client.commonclient.utility;

import java.util.Random;

/**
 * @version 1.0
 * @since 12-5-2016
 */
public class TestHelper {

    public static String createRandomValidUsername() {
        char[] possibleChars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char c = possibleChars[random.nextInt(possibleChars.length)];
            sb.append(c);
        }
        return (sb.toString());
    }
}
