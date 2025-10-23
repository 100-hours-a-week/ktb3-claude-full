package ktb.handler.context.payload;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public record CommentDeletePayload (
        Long articleId,
        Long commentId
) {
}
