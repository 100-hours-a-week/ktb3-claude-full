package ktb.exception;

import ktb.constant.MessageConstant.User;

public class AuthenticateException extends RuntimeException{
    public AuthenticateException() {
        super(User.AUTHENTICATION_FAILED);
    }
}
