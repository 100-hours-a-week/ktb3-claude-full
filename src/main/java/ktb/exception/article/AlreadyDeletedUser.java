package ktb.exception.article;

import ktb.constant.MessageConstant.User;

public class AlreadyDeletedUser extends RuntimeException{
    public AlreadyDeletedUser() {
        super(User.DELETED);
    }
}
