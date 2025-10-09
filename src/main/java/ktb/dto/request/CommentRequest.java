package ktb.dto.request;

import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @NotNull
        Long userAccountId,

        @NotNull
        String content
) {
}
