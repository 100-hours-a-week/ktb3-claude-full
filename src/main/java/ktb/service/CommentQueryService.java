package ktb.service;

import java.util.List;

import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.dto.CommentDto;
import ktb.exception.article.NoExistArticleException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comment 조회 전용 서비스 (CQS 패턴 - Query)
 * 읽기 작업만 수행하며, 데이터 변경 작업은 수행하지 않음
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {
    private final ArticleService articleService;
    private final CommentService commentService;

    /**
     * 게시글 ID로 댓글 목록 조회
     *
     * @param articleId 게시글 ID
     * @return 댓글 DTO 목록
     * @throws NoExistArticleException 게시글이 존재하지 않을 경우
     */
    public List<CommentDto> searchCommentsByArticleId(Long articleId) {
        Article article = articleService.findById(articleId)
                .orElseThrow(NoExistArticleException::new);
        List<ArticleComment> comments = commentService.findAllByArticle(article);

        return comments.stream()
                .map(CommentDto::from)
                .toList();
    }
}
