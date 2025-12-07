package ktb.handler.context;

import ktb.handler.context.payload.SoftDeletePayload;

public record SoftDeleteContext(
        Long traceId,
        SoftDeletePayload payload
) implements ContextData<SoftDeletePayload> {
}

