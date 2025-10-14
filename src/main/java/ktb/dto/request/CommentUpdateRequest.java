package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ktb.constant.MessageConstant.CommentMessage;

public record CommentUpdateRequest(
        @NotNull
        @JsonProperty("user_id")
        Long userAccountId,

        @NotNull
        @JsonProperty("comment_id")
        Long commentId,

        @NotBlank(message = CommentMessage.EMPTY_CONTENT)
        @JsonProperty("comment_content")
        String content
) {
}
