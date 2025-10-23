package ktb.handler.context;

public interface ContextData<T> {
    /**
     * 추적용 식별자 (주로 userId)
     */
    Long traceId();

    /**
     * 실제 데이터 페이로드
     */
    T payload();
}
