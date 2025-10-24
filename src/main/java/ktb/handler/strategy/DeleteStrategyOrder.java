package ktb.handler.strategy;

/**
 * 삭제 전략의 실행 순서를 정의하는 Enum
 *
 * 각 삭제 작업은 의존성에 따라 순서대로 실행됩니다:
 * - User 삭제: USER_DELETE(1) → USER_ARTICLES_DELETE(2) → USER_COMMENTS_DELETE(3)
 * - Article 삭제: ARTICLE_DELETE(1) → ARTICLE_COMMENTS_DELETE(2)
 * - Comment 삭제: COMMENT_DELETE(1)
 */
public enum DeleteStrategyOrder {
    /**
     * 1단계: User 엔티티 소프트 삭제
     * - 의존성: 없음
     * - 후속 작업: USER_ARTICLES_DELETE
     */
    USER_DELETE(1),

    /**
     * 2단계: User 가 작성한 모든 Articles 소프트 삭제
     * - 의존성: USER_DELETE 완료 필수
     * - 조건: 전체 User 삭제 시에만 실행
     */
    USER_ARTICLES_DELETE(2),

    /**
     * 3단계: User 가 작성한 모든 Comments 소프트 삭제
     * - 의존성: USER_ARTICLES_DELETE 완료 필수
     * - 조건: 전체 User 삭제 시에만 실행
     */
    USER_COMMENTS_DELETE(3),

    /**
     * 1단계: 단일 Article 소프트 삭제
     * - 의존성: 없음
     * - 후속 작업: ARTICLE_COMMENTS_DELETE
     */
    ARTICLE_DELETE(1),

    /**
     * 2단계: Article 에 속한 모든 Comments 소프트 삭제
     * - 의존성: ARTICLE_DELETE 완료 필수
     */
    ARTICLE_COMMENTS_DELETE(2),

    /**
     * 1단계: 단일 Comment 소프트 삭제
     * - 의존성: 없음
     */
    COMMENT_DELETE(1);

    private final int order;

    DeleteStrategyOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
