package ktb.exception.article;

import ktb.constant.MessageConstant.ArticleMessage;

public class NoExistArticleException extends RuntimeException{
    public NoExistArticleException() {
        super(ArticleMessage.NON_EXIST);
    }
}
