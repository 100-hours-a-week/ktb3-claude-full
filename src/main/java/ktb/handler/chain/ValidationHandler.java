package ktb.handler.chain;

import java.util.List;

import ktb.handler.AbstractHandler;
import ktb.handler.context.ContextData;
import ktb.handler.strategy.ValidationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 검증 Handler (CoR 2단계)
 *
 * 삭제 실행 전 비즈니스 규칙을 검증합니다.
 * 전략 패턴을 사용하여 컨텍스트별 검증 로직을 적용합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationHandler extends AbstractHandler<ContextData<?>> {
    private final List<ValidationStrategy<?>> validationStrategies;

    @Override
    public boolean handle(ContextData<?> context) {
        log.info("Validating context: {}", context.getClass().getSimpleName());

        // 적용 가능한 모든 검증 전략 실행
        validationStrategies.stream()
                .filter(strategy -> strategy.getSupportedContextType().isInstance(context))
                .forEach(strategy -> validateWithStrategy(strategy, context));

        // 검증 통과 시 다음 Handler 로 (Execution)
        return super.handle(context);
    }

    /**
     * 검증 전략을 타입 안전하게 실행
     */
    @SuppressWarnings("unchecked")
    private <C extends ContextData<?>> void validateWithStrategy(
            ValidationStrategy<?> strategy,
            ContextData<?> context) {
        ((ValidationStrategy<C>) strategy).validate((C) context);
    }
}
