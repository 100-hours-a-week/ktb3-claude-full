package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ktb.constant.MessageConstant.CommentMessage;

@Schema(description = "댓글 등록 요청 구조")
public record CommentRequest(
        @Schema(description = "댓글 내용", example = "댓글 내용")
        @NotBlank(message = CommentMessage.EMPTY_CONTENT)
        @JsonProperty("comment_content")
        String content
) {
}
