package ktb.handler.strategy.impl;

import ktb.domain.Article;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.DeleteStrategyOrder;
import ktb.service.ArticleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final ArticleService articleService;

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
    @Transactional
    public void execute(SoftDeleteContext context) {
        Long articleId = context.payload().articleId();

        Article article = articleService.findById(articleId)
                .orElseThrow(AlreadyDeletedArticle::new);

        article.softDelete();

        log.info("Article {} deleted", articleId);
    }
}
