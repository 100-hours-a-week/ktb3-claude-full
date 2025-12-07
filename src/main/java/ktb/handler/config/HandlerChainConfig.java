package ktb.handler.config;

import ktb.handler.AbstractHandler;
import ktb.handler.chain.execution.ArticleDeleteExecutionHandler;
import ktb.handler.chain.AuditHandler;
import ktb.handler.chain.execution.CommentDeleteExecutionHandler;
import ktb.handler.chain.execution.UserDeleteExecutionHandler;
import ktb.handler.context.ContextData;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handler Chain 설정
 *
 * CoR + 전략 패턴 하이브리드 구조:
 * - CoR: 단계별 책임 분리 (Execution → Audit)
 * - 전략: 각 단계 내에서 도메인별 알고리즘 자동 선택
 *
 * 도메인별로 분리된 Execution Handler를 사용하여 각 도메인의 삭제 작업을 처리합니다:
 * - User: UserDeleteExecutionHandler
 * - Article: ArticleDeleteExecutionHandler
 * - Comment: CommentDeleteExecutionHandler
 */
@Configuration
@RequiredArgsConstructor
public class HandlerChainConfig {
    private final UserDeleteExecutionHandler userDeleteExecutionHandler;
    private final ArticleDeleteExecutionHandler articleDeleteExecutionHandler;
    private final CommentDeleteExecutionHandler commentDeleteExecutionHandler;
    private final AuditHandler auditHandler;

    /**
     * User 삭제 Handler 체인
     *
     * 1. Execution: User 삭제 실행 (UserDeleteStrategy, UserArticlesDeleteStrategy, UserCommentsDeleteStrategy)
     * 2. Audit: 감사 로그 기록
     *
     * @return User 삭제 Handler 체인
     */
    @Bean(name = "userDeleteHandlerChain")
    public AbstractHandler<ContextData<?>> userDeleteHandlerChain() {
        return AbstractHandler.chainOf(
                userDeleteExecutionHandler,     // 1단계: User 삭제 실행
                auditHandler                    // 2단계: 감사 로그
        );
    }

    /**
     * Article 삭제 Handler 체인
     *
     * 1. Execution: Article 삭제 실행 (SingleArticleDeleteStrategy, ArticleCommentsDeleteStrategy)
     * 2. Audit: 감사 로그 기록
     *
     * @return Article 삭제 Handler 체인
     */
    @Bean(name = "articleDeleteHandlerChain")
    public AbstractHandler<ContextData<?>> articleDeleteHandlerChain() {
        return AbstractHandler.chainOf(
                articleDeleteExecutionHandler,     // 1단계: Article 삭제 실행
                auditHandler                       // 2단계: 감사 로그
        );
    }

    /**
     * Comment 삭제 Handler 체인
     *
     * 1. Execution: Comment 삭제 실행 (SingleCommentDeleteStrategy)
     * 2. Audit: 감사 로그 기록
     *
     * @return Comment 삭제 Handler 체인
     */
    @Bean(name = "commentDeleteHandlerChain")
    public AbstractHandler<ContextData<?>> commentDeleteHandlerChain() {
        return AbstractHandler.chainOf(
                commentDeleteExecutionHandler,      // 1단계: Comment 삭제 실행
                auditHandler                        // 2단계: 감사 로그
        );
    }
}

