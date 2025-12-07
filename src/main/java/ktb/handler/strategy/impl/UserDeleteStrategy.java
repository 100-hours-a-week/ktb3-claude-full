package ktb.handler.strategy.impl;

import ktb.domain.UserAccount;
import ktb.exception.article.AlreadyDeletedUser;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.DeleteStrategyOrder;
import ktb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User 삭제 전략
 *
 * User 엔티티를 소프트 삭제합니다.
 * - 실행 순서: 1 (가장 먼저 실행)
 * - 조건: 항상 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeleteStrategy implements DeleteExecutionStrategy<SoftDeleteContext> {
    private final UserRepository userRepository;

    @Override
    public Class<SoftDeleteContext> getSupportedContextType() {
        return SoftDeleteContext.class;
    }

    @Override
    public DeleteStrategyOrder getStrategyOrder() {
        return DeleteStrategyOrder.USER_DELETE;
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

        UserAccount user = userRepository.findById(userId)
                .orElseThrow(AlreadyDeletedUser::new);

        user.softDelete();

        log.info("User {} deleted", userId);
    }
}
