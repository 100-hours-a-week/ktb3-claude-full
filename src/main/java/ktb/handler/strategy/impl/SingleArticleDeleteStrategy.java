package ktb.handler.strategy.impl;

import ktb.domain.Article;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.DeleteStrategyOrder;
import ktb.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 단일 Article 삭제 전략
 *
 * 특정 Article 을 소프트 삭제합니다.
 * - 실행 순서: 1 (가장 먼저 실행)
 * - 조건: articleId가 있을 때만 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SingleArticleDeleteStrategy implements DeleteExecutionStrategy<SoftDeleteContext> {
    private final ArticleRepository articleRepository;

    @Override
    public Class<SoftDeleteContext> getSupportedContextType() {
        return SoftDeleteContext.class;
    }

    @Override
    public DeleteStrategyOrder getStrategyOrder() {
        return DeleteStrategyOrder.ARTICLE_DELETE;
    }

    @Override
    public boolean shouldExecute(SoftDeleteContext context) {
        // payload가 있고 articleId가 있을 때만 실행 (단일 Article 삭제)
        return context.payload() != null && context.payload().articleId() != null;
    }

    @Override
    public void execute(SoftDeleteContext context) {
        Long articleId = context.payload().articleId();

        Article article = articleRepository.findById(articleId)
                .orElseThrow(AlreadyDeletedArticle::new);

        article.softDelete();

        // TODO: Save 실패 시 Rollback 가능하게 save 결과를 받아올 필요가 있음
        articleRepository.save(article);

        log.info("Article {} deleted", articleId);
    }
}
