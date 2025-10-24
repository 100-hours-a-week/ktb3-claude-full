package ktb.handler.chain;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ktb.handler.AbstractHandler;
import ktb.handler.context.ContextData;
import ktb.handler.strategy.DeleteExecutionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 실행 Handler (CoR 3단계)
 *
 * 전략 패턴을 사용하여 컨텍스트 타입에 맞는 삭제 전략들을 자동 선택하고 실행합니다.
 * - 타입 매칭: getSupportedContextType()으로 적용 가능한 전략 필터링
 * - 조건 확인: shouldExecute()로 추가 조건 체크
 * - 순서 보장: getOrder()로 의존성 순서에 따라 정렬 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionHandler extends AbstractHandler<ContextData<?>> {
    private final List<DeleteExecutionStrategy<?>> executionStrategies;

    @Override
    @SuppressWarnings("unchecked")
    public boolean handle(ContextData<?> context) {
        log.info("Executing delete operation for: {}", context.getClass().getSimpleName());

        // 1. 컨텍스트 타입에 맞는 전략들 필터링
        // 2. 조건(shouldExecute) 확인
        // 3. 실행 순서대로 정렬
        List<DeleteExecutionStrategy<?>> applicableStrategies = executionStrategies.stream()
                .filter(strategy -> strategy.getSupportedContextType().isInstance(context))
                .filter(strategy -> shouldExecuteStrategy(strategy, context))
                .sorted(Comparator.comparingInt(DeleteExecutionStrategy::getOrder))
                .toList();

        if (applicableStrategies.isEmpty()) {
            throw new IllegalStateException("No strategy found for context: " + context.getClass());
        }

        // 실행 순서 로그 (디버깅용)
        log.debug("Execution order: {}",
                applicableStrategies.stream()
                        .map(s -> s.getStrategyOrder().name())
                        .collect(Collectors.joining(" → ")));

        // 전략 순차 실행
        for (DeleteExecutionStrategy<?> strategy : applicableStrategies) {
            try {
                log.debug("Executing strategy: {}", strategy.getStrategyOrder().name());
                executeStrategy(strategy, context);
            } catch (Exception e) {
                log.error("Strategy {} execution failed",
                        strategy.getStrategyOrder().name(), e);
                // 실패 시 체인 중단
                return false;
            }
        }

        return super.handle(context);
    }

    /**
     * 전략의 shouldExecute() 메서드를 타입 안전하게 호출
     */
    @SuppressWarnings("unchecked")
    private <C extends ContextData<?>> boolean shouldExecuteStrategy(
            DeleteExecutionStrategy<?> strategy,
            ContextData<?> context) {
        return ((DeleteExecutionStrategy<C>) strategy).shouldExecute((C) context);
    }

    /**
     * 전략을 타입 안전하게 실행 (형변환 불필요!)
     */
    @SuppressWarnings("unchecked")
    private <C extends ContextData<?>> void executeStrategy(
            DeleteExecutionStrategy<?> strategy,
            ContextData<?> context) {
        ((DeleteExecutionStrategy<C>) strategy).execute((C) context);
    }
}
