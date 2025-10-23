package ktb.service;

import ktb.dto.CommentDto;
import ktb.handler.AbstractHandler;
import ktb.handler.context.CommentDeleteContext;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.context.payload.CommentDeletePayload;
import ktb.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleRepository articleRepository;
    private final AbstractHandler<CommentDeleteContext> commentDeleteHandlerChain;

    public CommentDto addComment(CommentDto request, String nickname) {
        return CommentDto.from(articleRepository.addComment(request.articleId(), request.content(), request.createBy(), nickname));
    }

    public void update(CommentDto request) {
        // 인가는 @Authorized AOP 에서 확인
        articleRepository.updateComment(request.articleId(), request.id(), request.content());
    }

    public void delete(Long userId, CommentDto request) {
        CommentDeletePayload payload = new CommentDeletePayload(request.articleId(), request.id());
        CommentDeleteContext context = new CommentDeleteContext(userId, payload);

        // 인가는 @Authorized AOP 에서 확인
        commentDeleteHandlerChain.handle(context);
    }
}
