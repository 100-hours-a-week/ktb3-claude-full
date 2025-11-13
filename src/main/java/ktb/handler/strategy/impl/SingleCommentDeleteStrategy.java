package ktb.handler.strategy.impl;

import ktb.domain.ArticleComment;
import ktb.handler.context.CommentDeleteContext;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.DeleteStrategyOrder;
import ktb.service.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final CommentService commentService;

    @Override
    public Class<CommentDeleteContext> getSupportedContextType() {
        return CommentDeleteContext.class;
    }

    @Override
    public DeleteStrategyOrder getStrategyOrder() {
        return DeleteStrategyOrder.COMMENT_DELETE;
    }

    @Override
    public boolean shouldExecute(CommentDeleteContext context) {
        // 단일 Comment 삭제: commentId가 있을 때만 실행
        return (context.payload().commentId() != null);
    }

    @Override
    @Transactional
    public void execute(CommentDeleteContext context) {
        Long commentId = context.payload().commentId();

        ArticleComment comment = commentService.findById(commentId).orElseThrow();
        comment.softDelete();

        log.info("Comment {} in Article {} deleted", commentId, comment.getArticle().getId());
    }
}
