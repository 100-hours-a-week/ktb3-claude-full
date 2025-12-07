package ktb.handler.chain;

import ktb.handler.AbstractHandler;
import ktb.handler.context.ContextData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 권한 확인 Handler (CoR 1단계)
 *
 * 삭제 작업 수행 전 사용자 권한을 확인합니다.
 * 현재는 기본적인 로깅만 수행하며, 실제 권한 체크는 AOP(@Authorized)에서 처리됩니다.
 *
 * TODO: 필요 시 추가적인 권한 검증 로직 구현
 */
@Slf4j
@Component
public class AuthorizationHandler extends AbstractHandler<ContextData<?>> {

    @Override
    public boolean handle(ContextData<?> context) {
        Long userId = context.traceId();

        log.info("Authorizing user {} for {} operation",
                userId, context.getClass().getSimpleName());

        // 권한 확인 로직 (현재는 통과)
        // 실제 권한 체크는 Service 레이어의 @Authorized AOP 에서 처리됨

        // 다음 Handler 로 (Validation)
        return super.handle(context);
    }
}
