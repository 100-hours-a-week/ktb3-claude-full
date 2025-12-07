package ktb.handler.context;

import ktb.handler.context.payload.CommentDeletePayload;

public record CommentDeleteContext(
        Long traceId,
        CommentDeletePayload payload
) implements ContextData<CommentDeletePayload> {
}
