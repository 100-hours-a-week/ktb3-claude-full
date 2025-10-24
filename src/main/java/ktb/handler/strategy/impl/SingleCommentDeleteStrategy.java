package ktb.handler.strategy.impl;

import ktb.domain.Article;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.handler.context.CommentDeleteContext;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.DeleteStrategyOrder;
import ktb.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 단일 Comment 삭제 전략
 *
 * 특정 Comment 를 소프트 삭제합니다.
 * - 실행 순서: 1
 * - 조건: 항상 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SingleCommentDeleteStrategy implements DeleteExecutionStrategy<CommentDeleteContext> {
    private final ArticleRepository articleRepository;

    @Override
    public Class<CommentDeleteContext> getSupportedContextType() {
        return CommentDeleteContext.class;
    }

    @Override
    public DeleteStrategyOrder getStrategyOrder() {
        return DeleteStrategyOrder.COMMENT_DELETE;
    }

    @Override
    public void execute(CommentDeleteContext context) {
        Long articleId = context.payload().articleId();

        Article article = articleRepository.findById(articleId)
                .orElseThrow(AlreadyDeletedArticle::new);

        Long commentId = context.payload().commentId();

        article.softDeleteComment(commentId);

        // TODO: Save 실패 시 Rollback 가능하게 save 결과를 받아올 필요가 있음
        articleRepository.save(article);

        log.info("Comment {} in Article {} deleted", commentId, articleId);
    }
}
