package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ktb.constant.MessageConstant.CommentMessage;

public record CommentRequest(
        @NotNull
        @JsonProperty("user_id")
        Long userAccountId,

        @NotBlank(message = CommentMessage.EMPTY_CONTENT)
        @JsonProperty("comment_content")
        String content
) {
}
