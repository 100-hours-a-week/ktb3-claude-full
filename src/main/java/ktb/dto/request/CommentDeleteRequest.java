package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CommentDeleteRequest(
        @NotNull
        @JsonProperty("user_id")
        Long userAccountId,

        @NotNull
        @JsonProperty("comment_id")
        Long commentId
) {
}
