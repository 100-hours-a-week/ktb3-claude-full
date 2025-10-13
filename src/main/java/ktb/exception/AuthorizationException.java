package ktb.exception;

import ktb.constant.MessageConstant.User;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException() {
        super(User.ACCESS_DENIED);
    }
}
