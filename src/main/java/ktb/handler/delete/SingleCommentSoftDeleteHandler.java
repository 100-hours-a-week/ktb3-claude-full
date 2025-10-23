package ktb.handler.delete;

import ktb.domain.Article;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.handler.AbstractHandler;
import ktb.handler.context.CommentDeleteContext;
import ktb.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SingleCommentSoftDeleteHandler extends AbstractHandler<CommentDeleteContext> {
    private final ArticleRepository articleRepository;

    @Override
    public boolean handle(CommentDeleteContext context) {
        Long articleId = context.payload().articleId();

        Article article =
                articleRepository.findById(articleId)
                        .orElseThrow(AlreadyDeletedArticle::new);

        Long commentId = context.payload().commentId();

        article.softDeleteComment(commentId);

        // TODO: Save 실패 시 Rollback 가능하게 save 결과를 받아올 필요가 있음
        articleRepository.save(article);

        return super.handle(context);
    }
}
