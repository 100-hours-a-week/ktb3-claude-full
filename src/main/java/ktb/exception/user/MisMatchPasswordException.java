package ktb.exception.user;

import ktb.constant.MessageConstant.Password;

public class MisMatchPasswordException extends RuntimeException {
    public MisMatchPasswordException() {
        super(Password.CONFIRMATION_MISMATCH);
    }
}
