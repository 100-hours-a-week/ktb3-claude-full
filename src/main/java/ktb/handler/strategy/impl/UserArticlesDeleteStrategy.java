package ktb.handler.strategy.impl;

import java.util.List;

import ktb.domain.Article;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.DeleteStrategyOrder;
import ktb.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User 의 모든 Articles 삭제 전략
 *
 * User 가 작성한 모든 Article 을 소프트 삭제합니다.
 * - 실행 순서: 2 (User 삭제 후 실행)
 * - 조건: User 전체 삭제 시에만 실행 (articleId == null)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserArticlesDeleteStrategy implements DeleteExecutionStrategy<SoftDeleteContext> {
    private final ArticleService articleService;

    @Override
    public Class<SoftDeleteContext> getSupportedContextType() {
        return SoftDeleteContext.class;
    }

    @Override
    public DeleteStrategyOrder getStrategyOrder() {
        return DeleteStrategyOrder.USER_ARTICLES_DELETE;
    }

    @Override
    public boolean shouldExecute(SoftDeleteContext context) {
        // traceId가 있고 payload 의 articleId가 null 인 경우만 실행 (User 전체 삭제)
        return context.traceId() != null
            && (context.payload() == null || context.payload().articleId() == null);
    }

    @Override
    @Transactional
    public void execute(SoftDeleteContext context) {
        Long userId = context.traceId();

        List<Article> articles = articleService.findByCreateBy_Id(userId);

        // Article softDelete
        articles.stream()
                .filter(article -> !article.isDelete())
                .forEach(Article::softDelete);

        // 저장
        articleService.saveAll(articles);

        log.info("User {} articles deleted: {} articles", userId, articles.size());
    }
}
