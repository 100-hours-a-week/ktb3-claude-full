package ktb.util;

import java.util.function.Predicate;

public class PasswordValidator {
    public boolean validatePassword(String pwd) {
        // 패스워드가 8 이상인지 확인하는 Predicate
        Predicate<String> lengthPredicate =
                password -> password != null && password.length() >= 8;

        Predicate<String> upperAndLowerCasePredicate =
                password -> password != null &&
                        password.matches(".*[A-Z].*") &&
                        password.matches(".*[a-z].*");

        Predicate<String> specialCharPredicate =
                password -> password != null &&
                        password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        Predicate<String> passwordPolicy =
                lengthPredicate.and(upperAndLowerCasePredicate).and(specialCharPredicate);

        return passwordPolicy.test(pwd);
    }
}
