package ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ktb.domain.ArticleComment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 댓글 정보 DTO
 */
public record CommentDetailDto(
        @JsonProperty("comment_id")
        String commentId,

        @JsonProperty("user_nickname")
        String userNickname,

        @JsonProperty("comment_content")
        String commentContent,

        @JsonProperty("last_modified_date")
        String lastModifiedDate
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CommentDetailDto from(ArticleComment comment) {
        LocalDateTime lastModified = comment.getUpdateAt() != null
                ? comment.getUpdateAt()
                : comment.getCreateAt();

        return new CommentDetailDto(
                String.valueOf(comment.getId()),
                comment.getCreateBy().getNickname(),
                comment.getContent(),
                lastModified.format(FORMATTER)
        );
    }
}
