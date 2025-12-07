package ktb.handler.strategy;

import ktb.handler.context.ContextData;

/**
 * 삭제 실행 전략 인터페이스
 *
 * 각 도메인(User, Article, Comment)별 삭제 로직을 캡슐화합니다.
 * 제네릭을 사용하여 타입 안전성을 보장하고 형변환을 제거합니다.
 *
 * @param <C> 처리할 ContextData 타입
 */
public interface DeleteExecutionStrategy<C extends ContextData<?>> {

    /**
     * 이 전략이 처리 가능한 컨텍스트 타입을 반환
     *
     * @return 처리 가능한 Context 클래스
     */
    Class<C> getSupportedContextType();

    /**
     * 전략의 실행 순서를 반환
     * Enum 으로 중앙 관리되어 전체 실행 흐름을 한눈에 파악 가능
     *
     * @return 실행 순서 Enum
     */
    DeleteStrategyOrder getStrategyOrder();

    /**
     * 이 전략을 실행할지 여부를 결정
     * 기본적으로는 항상 실행하지만, 조건부 실행이 필요한 경우 오버라이드
     *
     * 예: UserArticlesDeleteStrategy: articleId가 null 일 때만 실행
     *
     * @param context 컨텍스트
     * @return true: 실행, false: 스킵
     */
    default boolean shouldExecute(C context) {
        return true;
    }

    /**
     * 실제 삭제 로직을 수행
     * 제네릭 타입으로 형변환 없이 안전하게 컨텍스트 사용
     *
     * @param context 삭제 컨텍스트 (형변환 불필요!)
     */
    void execute(C context);

    /**
     * 정렬을 위한 숫자 값 반환
     *
     * @return 실행 순서 (낮을수록 먼저 실행)
     */
    default int getOrder() {
        return getStrategyOrder().getOrder();
    }
}
