package ktb.handler.config;

import ktb.handler.AbstractHandler;
import ktb.handler.context.CommentDeleteContext;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.delete.ArticleScopeCommentsSoftDeleteHandler;
import ktb.handler.delete.SingleCommentSoftDeleteHandler;
import ktb.handler.delete.UserScopeArticlesSoftDeleteHandler;
import ktb.handler.delete.UserScopeCommentsSoftDeleteHandler;
import ktb.handler.delete.SingleArticleSoftDeleteHandler;
import ktb.handler.delete.UserSoftDeleteHandler;
import ktb.repository.ArticleRepository;
import ktb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HandlerChainConfig {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    /**
     * User 삭제 시 사용되는 Handler 체인
     * User -> Article -> Comment 순서로 소프트 삭제 처리
     *
     * @return User 삭제용 Handler 체인
     */
    @Bean(name = "userDeleteHandlerChain")
    public AbstractHandler<SoftDeleteContext> userDeleteHandlerChain() {
        return AbstractHandler.chainOf(
                new UserSoftDeleteHandler(userRepository),
                new UserScopeArticlesSoftDeleteHandler(articleRepository),
                new UserScopeCommentsSoftDeleteHandler(articleRepository)
        );
    }

    /**
     * Article 삭제 시 사용되는 Handler 체인
     * Article -> Comment 순서로 소프트 삭제 처리
     *
     * @return Article 삭제용 Handler 체인
     */
    @Bean(name = "articleDeleteHandlerChain")
    public AbstractHandler<SoftDeleteContext> articleDeleteHandlerChain() {
        return AbstractHandler.chainOf(
                new SingleArticleSoftDeleteHandler(articleRepository),
                new ArticleScopeCommentsSoftDeleteHandler(articleRepository)
        );
    }

    /**
     * Comment 삭제 시 사용되는 Handler 체인
     * Comment 순서로 소프트 삭제 처리
     *
     * @return Comment 삭제용 Handler 체인
     */
    @Bean(name = "commentDeleteHandlerChain")
    public AbstractHandler<CommentDeleteContext> commentDeleteHandlerChain() {
        return AbstractHandler.chainOf(
                new SingleCommentSoftDeleteHandler(articleRepository)
        );
    }
}
