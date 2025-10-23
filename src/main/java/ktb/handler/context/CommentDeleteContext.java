package ktb.handler.context;

import ktb.handler.context.payload.CommentDeletePayload;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public record CommentDeleteContext(
        Long traceId,
        CommentDeletePayload payload
) implements ContextData<CommentDeletePayload> {
}
