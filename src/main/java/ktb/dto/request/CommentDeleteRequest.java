package ktb.dto.request;

import jakarta.validation.constraints.NotNull;

public record CommentDeleteRequest(
        @NotNull
        Long articleId,

        @NotNull
        Long userAccountId,

        @NotNull
        Long commentId
) {
}
