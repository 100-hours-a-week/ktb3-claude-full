package ktb.handler.context;

import ktb.handler.context.payload.SoftDeletePayload;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public record SoftDeleteContext(
        Long traceId,
        SoftDeletePayload payload
) implements ContextData<SoftDeletePayload> {
}

