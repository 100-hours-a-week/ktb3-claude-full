package ktb.dto;

import ktb.domain.ArticleComment;
import lombok.Builder;

@Builder
public record CommentDto(
        Long id,
        Long articleId,
        String content,
        Long createBy
) {
    public static CommentDto from(ArticleComment comment) {
        return new CommentDto(comment.getId(), comment.getArticleId(), comment.getContent(), comment.getCreateBy());
    }

    public static CommentDto ofUpdate(Long commentId, Long articleId, String content, Long userId) {
        return new CommentDto(commentId, articleId, content, userId);
    }
}
