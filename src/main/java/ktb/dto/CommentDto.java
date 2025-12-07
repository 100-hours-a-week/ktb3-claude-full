package ktb.dto;

import ktb.domain.Article;
import ktb.domain.ArticleComment;
import lombok.Builder;

@Builder
public record CommentDto(
        Long id,
        Long articleId,
        String content,
        Long createBy,
        String createNickName
) {
    public static CommentDto from(ArticleComment comment) {
        return new CommentDto(comment.getId(), comment.getArticle().getId(), comment.getContent(), comment.getCreateBy().getId(),
                comment.getCreateBy().getNickname());
    }

    public static CommentDto ofUpdate(Long commentId, Long articleId, String content, Long userId, String userNickname) {
        return new CommentDto(commentId, articleId, content, userId, userNickname);
    }

    public ArticleComment toEntity(Article article) {
        return ArticleComment.init(
                article,
                this.id,
                this.content,
                this.createBy
        );
    }
}
