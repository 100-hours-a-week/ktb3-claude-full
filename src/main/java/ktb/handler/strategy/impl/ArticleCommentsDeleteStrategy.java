package ktb.handler.strategy.impl;

import java.util.List;
import java.util.stream.Collectors;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.DeleteStrategyOrder;
import ktb.repository.ArticleRepository;
import ktb.service.ArticleService;
import ktb.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Article 의 모든 Comments 삭제 전략
 *
 * 특정 Article 에 속한 모든 Comment 를 소프트 삭제합니다.
 * - 실행 순서: 2 (Article 삭제 후 실행)
 * - 조건: articleId가 있을 때만 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleCommentsDeleteStrategy implements DeleteExecutionStrategy<SoftDeleteContext> {
    private final ArticleService articleService;

    @Override
    public Class<SoftDeleteContext> getSupportedContextType() {
        return SoftDeleteContext.class;
    }

    @Override
    public DeleteStrategyOrder getStrategyOrder() {
        return DeleteStrategyOrder.ARTICLE_COMMENTS_DELETE;
    }

    @Override
    public boolean shouldExecute(SoftDeleteContext context) {
        // payload 가 있고 articleId가 있을 때만 실행 (단일 Article 삭제)
        return context.payload() != null && context.payload().articleId() != null;
    }

    @Override
    @Transactional
    public void execute(SoftDeleteContext context) {
        Article article = articleService.findById(context.payload().articleId())
                .orElseThrow(AlreadyDeletedArticle::new);

        article.getComments().forEach(ArticleComment::softDelete);

        log.info("Article {} comments deleted", context.payload().articleId());
    }
}
