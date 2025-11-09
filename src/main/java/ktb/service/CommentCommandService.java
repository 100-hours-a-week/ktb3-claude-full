package ktb.service;

import java.util.List;
import java.util.stream.Collectors;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import ktb.dto.CommentDto;
import ktb.dto.UserAccountDto;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.exception.article.AlreadyDeletedComment;
import ktb.exception.article.NoExistArticleException;
import ktb.handler.AbstractHandler;
import ktb.handler.context.CommentDeleteContext;
import ktb.handler.context.ContextData;
import ktb.handler.context.payload.CommentDeletePayload;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comment 수정 전용 서비스 (CQS 패턴 - Command)
 * 데이터 변경 작업(생성, 수정, 삭제)만 수행하며, 조회 작업은 수행하지 않음
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {
    private final ArticleService articleService;
    private final CommentService commentService;
    private final AbstractHandler<ContextData<?>> commentDeleteHandlerChain;

    /**
     * 댓글 추가
     *
     * @param request 댓글 생성 정보
     * @return 저장된 댓글 DTO
     * @throws NoExistArticleException 게시글이 존재하지 않을 경우
     */
    public CommentDto addComment(CommentDto request) {
        Article article = articleService.findById(request.articleId())
                .orElseThrow(NoExistArticleException::new);

        if (article.isDelete()) {
            throw new AlreadyDeletedArticle();
        }

        commentService.save(request.toEntity(article));

        return request;
    }

    /**
     * 댓글 수정 (DTO 기반)
     *
     * @param dto 수정할 댓글 정보
     * @return 수정된 댓글 DTO
     * @throws NoExistArticleException 게시글이 존재하지 않을 경우
     */
    public CommentDto save(CommentDto dto) {
        Article article = articleService.findById(dto.articleId())
                .orElseThrow(NoExistArticleException::new);

        if (article.isDelete()) {
            throw new AlreadyDeletedArticle();
        }

        ArticleComment comment = commentService.findById(dto.id()).orElseThrow(NoExistArticleException::new);

        if (comment.isDelete()) {
            throw new AlreadyDeletedComment();
        }

        comment.update(dto.content());

        return dto;
    }

    /**
     * 댓글 수정 (엔티티 기반)
     *
     * @param comment 수정할 댓글 엔티티
     * @return 수정된 댓글 DTO
     */
    public CommentDto save(ArticleComment comment) {
        return CommentDto.from(commentService.save(comment));
    }

    /**
     * 댓글 삭제 (Soft Delete)
     *
     * @param userId 요청 사용자 ID
     * @param request 삭제할 댓글 정보
     */
    public void delete(Long userId, CommentDto request) {
        CommentDeletePayload payload = new CommentDeletePayload(request.articleId(), request.id());
        CommentDeleteContext context = new CommentDeleteContext(userId, payload);

        // Handler 체인을 통한 소프트 삭제 처리 (Authorization → Validation → Execution → Audit)
        // Execution 단계에서 SingleCommentDeleteStrategy가 실행
        // 인가는 @Authorized AOP 에서 확인
        commentDeleteHandlerChain.handle(context);
    }

    /**
     * User 가 생성한 모든 댓글 정보 조회
     *
     * @param userId 요청 사용자 ID
     * @return 유저가 생성한 댓글들
     */
    public List<CommentDto> findAllByUserId(Long userId) {
        List<ArticleComment> comments = commentService.findAllByUserId(userId);

        return comments.stream()
                .map(CommentDto::from)
                .collect(Collectors.toList());
    }
}
