package ktb.handler.chain.execution;

import java.util.List;

import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.impl.SingleCommentDeleteStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Comment 삭제 실행 Handler
 *
 * Comment 도메인의 삭제 작업을 담당합니다:
 * 1. SingleCommentDeleteStrategy - Comment 엔티티 소프트 삭제
 *
 * 실행 조건과 순서는 각 Strategy에서 관리됩니다.
 */
@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommentDeleteExecutionHandler extends BaseExecutionHandler {

    private final SingleCommentDeleteStrategy singleCommentDeleteStrategy;

    @Override
    protected List<DeleteExecutionStrategy<?>> getStrategies() {
        return List.of(singleCommentDeleteStrategy);
    }

    @Override
    protected String getHandlerName() {
        return "CommentDeleteExecutionHandler";
    }
}
