package ktb.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptEncoder {
    public static String encode(String text) {
        return BCrypt.hashpw(text, BCrypt.gensalt());
    }

    public static boolean matches(String text, String origin) {
        return BCrypt.checkpw(text, origin);
    }
}
