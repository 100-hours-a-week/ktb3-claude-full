package ktb.handler.strategy;

import ktb.handler.context.ContextData;

/**
 * 감사 로그 전략 인터페이스
 *
 * 삭제 작업 후 감사 로그를 기록합니다.
 * 여러 전략을 동시에 적용 가능 (DB 로그, 파일 로그 등)
 *
 * @param <C> 처리할 ContextData 타입
 */
public interface AuditStrategy<C extends ContextData<?>> {

    /**
     * 이 전략이 처리 가능한 컨텍스트 타입을 반환
     *
     * @return 처리 가능한 Context 클래스
     */
    Class<C> getSupportedContextType();

    /**
     * 감사 로그 기록
     *
     * @param context 로그를 기록할 컨텍스트
     */
    void audit(C context);
}
