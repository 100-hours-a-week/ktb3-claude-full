package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 삭제 요청 구조")
public record CommentDeleteRequest(
        @Schema(description = "삭제 시도한 유저 ID(Sequential ID)", example = "1")
        @NotNull
        @JsonProperty("user_id")
        Long userAccountId,

        @Schema(description = "수정 대상 댓글 ID(Sequential ID)", example = "1")
        @NotNull
        @JsonProperty("comment_id")
        Long commentId
) {
}
