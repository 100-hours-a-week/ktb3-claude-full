package ktb.handler.chain.execution;

import java.util.List;

import ktb.handler.chain.execution.BaseExecutionHandler;
import ktb.handler.strategy.DeleteExecutionStrategy;
import ktb.handler.strategy.impl.ArticleCommentsDeleteStrategy;
import ktb.handler.strategy.impl.SingleArticleDeleteStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Article 삭제 실행 Handler
 *
 * Article 도메인의 삭제 작업을 담당합니다:
 * 1. SingleArticleDeleteStrategy - Article 엔티티 소프트 삭제
 * 2. ArticleCommentsDeleteStrategy - Article에 속한 모든 Comments 삭제
 *
 * 실행 조건과 순서는 각 Strategy에서 관리됩니다.
 */
@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ArticleDeleteExecutionHandler extends BaseExecutionHandler {

    private final SingleArticleDeleteStrategy singleArticleDeleteStrategy;
    private final ArticleCommentsDeleteStrategy articleCommentsDeleteStrategy;

    @Override
    protected List<DeleteExecutionStrategy<?>> getStrategies() {
        return List.of(
                singleArticleDeleteStrategy,
                articleCommentsDeleteStrategy
        );
    }

    @Override
    protected String getHandlerName() {
        return "ArticleDeleteExecutionHandler";
    }
}
