package ktb.handler.strategy;

import ktb.handler.context.ContextData;

/**
 * 검증 전략 인터페이스
 *
 * 삭제 작업 전 비즈니스 규칙 검증을 담당합니다.
 * 예: 이미 삭제된 엔티티인지, 권한이 있는지 등
 *
 * @param <C> 처리할 ContextData 타입
 */
public interface ValidationStrategy<C extends ContextData<?>> {

    /**
     * 이 전략이 처리 가능한 컨텍스트 타입을 반환
     *
     * @return 처리 가능한 Context 클래스
     */
    Class<C> getSupportedContextType();

    /**
     * 비즈니스 검증 로직 수행
     * 검증 실패 시 예외를 던져 체인 중단
     *
     * @param context 검증할 컨텍스트
     * @throws RuntimeException 검증 실패 시
     */
    void validate(C context);
}
