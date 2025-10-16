package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ktb.constant.MessageConstant.CommentMessage;

@Schema(description = "댓글 변경 요청 구조")
public record CommentUpdateRequest(
        @Schema(description = "변경 시도한 유저 ID(Sequential ID)", example = "1")
        @NotNull
        @JsonProperty("user_id")
        Long userAccountId,

        @Schema(description = "변경할 댓글 ID(Sequential ID)", example = "1")
        @NotNull
        @JsonProperty("comment_id")
        Long commentId,

        @Schema(description = "변경할 댓글 내용", example = "변경할 댓글 내용")
        @NotBlank(message = CommentMessage.EMPTY_CONTENT)
        @JsonProperty("comment_content")
        String content
) {
}
