package ktb.exception.article;

import ktb.constant.MessageConstant.CommentMessage;

public class AlreadyDeletedComment extends RuntimeException{
    public AlreadyDeletedComment() {
        super(CommentMessage.DELETED);
    }
}
