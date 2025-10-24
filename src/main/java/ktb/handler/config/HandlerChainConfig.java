package ktb.handler.config;

import ktb.handler.AbstractHandler;
import ktb.handler.chain.AuditHandler;
import ktb.handler.chain.AuthorizationHandler;
import ktb.handler.chain.ExecutionHandler;
import ktb.handler.chain.ValidationHandler;
import ktb.handler.context.ContextData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handler Chain 설정
 *
 * CoR + 전략 패턴 하이브리드 구조:
 * - CoR: 단계별 책임 분리 (Authorization → Validation → Execution → Audit)
 * - 전략: 각 단계 내에서 도메인별 알고리즘 자동 선택
 *
 * 모든 삭제 작업(User, Article, Comment)이 동일한 파이프라인을 사용하며,
 * Execution 단계에서 컨텍스트 타입에 따라 적절한 전략들이 자동으로 선택되어 실행됩니다.
 */
@Configuration
@RequiredArgsConstructor
public class HandlerChainConfig {
    private final AuthorizationHandler authorizationHandler;
    private final ValidationHandler validationHandler;
    private final ExecutionHandler executionHandler;
    private final AuditHandler auditHandler;

    /**
     * 통합 삭제 Handler 체인
     *
     * User, Article, Comment 등 모든 도메인에서 사용하는 단일 파이프라인:
     * 1. Authorization: 권한 확인
     * 2. Validation: 비즈니스 검증 (전략 선택)
     * 3. Execution: 실제 삭제 실행 (전략 선택 및 순서 보장)
     * 4. Audit: 감사 로그 기록 (전략 선택)
     *
     * @return 통합 삭제 Handler 체인
     */
    @Bean(name = "universalDeleteHandlerChain")
    public AbstractHandler<ContextData<?>> universalDeleteHandlerChain() {
        return AbstractHandler.chainOf(
                authorizationHandler,     // 1단계: 권한 확인
                validationHandler,        // 2단계: 비즈니스 검증
                executionHandler,         // 3단계: 실제 삭제 (전략 자동 선택)
                auditHandler              // 4단계: 감사 로그
        );
    }

    /**
     * 하위 호환성을 위한 User 삭제 체인 (Deprecated)
     *
     * @deprecated universalDeleteHandlerChain 사용 권장
     */
    @Deprecated
    @Bean(name = "userDeleteHandlerChain")
    public AbstractHandler<ContextData<?>> userDeleteHandlerChain() {
        return universalDeleteHandlerChain();
    }

    /**
     * 하위 호환성을 위한 Article 삭제 체인 (Deprecated)
     *
     * @deprecated universalDeleteHandlerChain 사용 권장
     */
    @Deprecated
    @Bean(name = "articleDeleteHandlerChain")
    public AbstractHandler<ContextData<?>> articleDeleteHandlerChain() {
        return universalDeleteHandlerChain();
    }

    /**
     * 하위 호환성을 위한 Comment 삭제 체인 (Deprecated)
     *
     * @deprecated universalDeleteHandlerChain 사용 권장
     */
    @Deprecated
    @Bean(name = "commentDeleteHandlerChain")
    public AbstractHandler<ContextData<?>> commentDeleteHandlerChain() {
        return universalDeleteHandlerChain();
    }
}
