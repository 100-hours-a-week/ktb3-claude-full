package ktb.dto.request;

import jakarta.validation.constraints.NotNull;

public record CommentDeleteRequest(
        @NotNull
        Long articleId,

        @NotNull
        Long userId,

        @NotNull
        Long commentId
) {
}
