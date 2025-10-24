package ktb.handler.context.payload;

import lombok.Builder;

@Builder
public record CommentDeletePayload (
        Long articleId,
        Long commentId
) {
}
