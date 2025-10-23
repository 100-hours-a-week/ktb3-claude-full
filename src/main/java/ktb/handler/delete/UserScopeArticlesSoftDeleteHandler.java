package ktb.handler.delete;

import java.util.List;
import ktb.domain.Article;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.handler.AbstractHandler;
import ktb.handler.context.SoftDeleteContext;
import ktb.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserScopeArticlesSoftDeleteHandler extends AbstractHandler<SoftDeleteContext> {
    private final ArticleRepository articleRepository;

    @Override
    public boolean handle(SoftDeleteContext context) {
        Long userId = context.traceId();

        List<Article> articles = articleRepository.findByCreateBy(userId)
                .stream()
                .toList();

        if (articles.isEmpty()) {
            throw new AlreadyDeletedArticle();
        }

        articles.forEach(Article::softDelete);

        // TODO: Save 실패 시 Rollback 가능하게 save 결과를 받아올 필요가 있음
        articles.forEach(articleRepository::save);

        return super.handle(context);
    }
}
