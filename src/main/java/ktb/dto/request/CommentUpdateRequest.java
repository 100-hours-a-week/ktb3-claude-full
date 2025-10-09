package ktb.dto.request;

import jakarta.validation.constraints.NotNull;

public record CommentUpdateRequest(
        @NotNull
        Long userAccountId,

        @NotNull
        Long commentId,

        @NotNull
        String content
) {
}
