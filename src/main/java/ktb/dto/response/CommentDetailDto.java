package ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import ktb.domain.ArticleComment;

import java.time.LocalDateTime;

/**
 * 댓글 정보 DTO
 */
public record CommentDetailDto(
        @JsonProperty("id")
        String commentId,

        @Schema(description = "댓글 작성한 유저 닉네임", example = "닉네임")
        @JsonProperty("user_nickname")
        String userNickname,

        @Schema(description = "댓글 내용", example = "유저가 작성한 댓글 내용")
        @JsonProperty("content")
        String commentContent,

        @Schema(description = "마지막 수정 일자", example = "2025-01-01T00:00:00")
        @JsonProperty("last_modified_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastModifiedDate,

        @Schema(description = "수정 가능 여부", example = "true")
        @JsonProperty("can_modify")
        Boolean canModify
) {
    public static CommentDetailDto from(ArticleComment comment, Long userId) {
        LocalDateTime lastModified = comment.getUpdateAt() != null
                ? comment.getUpdateAt()
                : comment.getCreateAt();
        boolean canModify = comment.getCreateBy().getId().equals(userId);

        return new CommentDetailDto(
                String.valueOf(comment.getId()),
                comment.getCreateBy().getNickname(),
                comment.getContent(),
                lastModified,
                canModify
        );
    }
}
