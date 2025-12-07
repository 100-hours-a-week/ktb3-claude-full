package ktb.exception.article;

import ktb.constant.MessageConstant.ArticleMessage;

public class AlreadyDeletedArticle extends RuntimeException{
    public AlreadyDeletedArticle() {
        super(ArticleMessage.DELETED);
    }
}
