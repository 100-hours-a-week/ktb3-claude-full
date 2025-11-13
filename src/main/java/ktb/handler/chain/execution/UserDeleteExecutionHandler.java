package ktb.handler.chain.execution;

import java.util.List;

import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.impl.UserArticlesDeleteStrategy;
import ktb.handler.strategy.impl.UserCommentsDeleteStrategy;
import ktb.handler.strategy.impl.UserDeleteStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * User 삭제 실행 Handler
 *
 * User 도메인의 삭제 작업을 담당합니다:
 * 1. UserDeleteStrategy - User 엔티티 소프트 삭제
 * 2. UserArticlesDeleteStrategy - User가 작성한 모든 Articles 삭제
 * 3. UserCommentsDeleteStrategy - User가 작성한 모든 Comments 삭제
 *
 * 실행 조건과 순서는 각 Strategy에서 관리됩니다.
 */
@Component
@RequiredArgsConstructor
public class UserDeleteExecutionHandler extends BaseExecutionHandler {

    private final UserDeleteStrategy userDeleteStrategy;
    private final UserArticlesDeleteStrategy userArticlesDeleteStrategy;
    private final UserCommentsDeleteStrategy userCommentsDeleteStrategy;

    @Override
    protected List<DeleteExecutionStrategy<?>> getStrategies() {
        return List.of(
                userDeleteStrategy,
                userArticlesDeleteStrategy,
                userCommentsDeleteStrategy
        );
    }

    @Override
    protected String getHandlerName() {
        return "UserDeleteExecutionHandler";
    }
}
