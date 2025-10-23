package ktb.handler.context.payload;

public record SoftDeletePayload (
        Long userId,
        Long articleId
) {
}
