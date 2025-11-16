package ktb.handler.chain;

import java.util.List;

import ktb.handler.AbstractHandler;
import ktb.handler.context.ContextData;
import ktb.handler.strategy.AuditStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 감사 로그 Handler (CoR 4단계 - 마지막)
 *
 * 삭제 작업 후 감사 로그를 기록합니다.
 * 여러 감사 전략을 동시에 적용 가능 (DB 로그, 파일 로그 등)
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditHandler extends AbstractHandler<ContextData<?>> {
    private final List<AuditStrategy<?>> auditStrategies;

    @Override
    public boolean handle(ContextData<?> context) {
        log.info("Auditing operation for: {}", context.getClass().getSimpleName());

        // 적용 가능한 모든 감사 전략 실행
        auditStrategies.stream()
                .filter(strategy -> strategy.getSupportedContextType().isInstance(context))
                .forEach(strategy -> auditWithStrategy(strategy, context));

        // 마지막 Handler 이므로 체인 종료
        return super.handle(context);
    }

    /**
     * 감사 전략을 타입 안전하게 실행
     */
    @SuppressWarnings("unchecked")
    private <C extends ContextData<?>> void auditWithStrategy(
            AuditStrategy<?> strategy,
            ContextData<?> context) {
        ((AuditStrategy<C>) strategy).audit((C) context);
    }
}
