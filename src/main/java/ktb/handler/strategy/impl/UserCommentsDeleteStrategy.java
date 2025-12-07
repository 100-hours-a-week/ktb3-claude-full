package ktb.handler.strategy.impl;

import java.util.List;

import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.dto.UserAccountDto;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.DeleteStrategyOrder;
import ktb.repository.ArticleCommentRepository;
import ktb.service.ArticleService;
import ktb.service.CommentService;
import ktb.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User 의 모든 Comments 삭제 전략
 *
 * User 가 작성한 모든 Comment 를 소프트 삭제합니다.
 * - 실행 순서: 3 (User Articles 삭제 후 실행)
 * - 조건: User 전체 삭제 시에만 실행 (articleId == null)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserCommentsDeleteStrategy implements DeleteExecutionStrategy<SoftDeleteContext> {
    private final CommentService commentService;
    private final ArticleCommentRepository commentRepository;

    @Override
    public Class<SoftDeleteContext> getSupportedContextType() {
        return SoftDeleteContext.class;
    }

    @Override
    public DeleteStrategyOrder getStrategyOrder() {
        return DeleteStrategyOrder.USER_COMMENTS_DELETE;
    }

    @Override
    public boolean shouldExecute(SoftDeleteContext context) {
        // traceId가 있고 payload의 articleId가 null인 경우만 실행 (User 전체 삭제)
        return context.traceId() != null
            && (context.payload() == null || context.payload().articleId() == null);
    }

    @Override
    @Transactional
    public void execute(SoftDeleteContext context) {
        Long userId = context.traceId();

        List<ArticleComment> comments = commentService.findAllByUserId(userId);

        // 이미 삭제된 댓글은 제외하고 softDelete
        comments.stream()
                .filter(comment -> !comment.isDelete())
                .forEach(ArticleComment::softDelete);

        log.info("User {} comments deleted: {} comments", userId, comments.size());
    }
}
