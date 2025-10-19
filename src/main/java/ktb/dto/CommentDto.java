package ktb.dto;

import ktb.domain.ArticleComment;

public record CommentDto(
        Long id,
        Long articleId,
        String content,
        Long createBy
) {
    public static CommentDto from(ArticleComment comment) {
        return new CommentDto(comment.getId(), comment.getArticleId(), comment.getContent(), comment.getCreateBy().getId());
    }

    public static CommentDto of(Long articleId, String content, Long userId) {
        return new CommentDto(null, articleId, content, userId);
    }

    public static CommentDto ofUpdate(Long commentId, Long articleId, String content, Long userId) {
        return new CommentDto(commentId, articleId, content, userId);
    }
}
