package ktb.exception.user;

import ktb.constant.MessageConstant.User;

public class NonExistUserException extends RuntimeException{
    public NonExistUserException() {
        super(User.NON_EXIST);
    }
}
