package ktb.dto;

public record PageInfoDto(
        Long endCursor,
        int size
) {
    public static PageInfoDto of(Long endCursor, int size) {
        return new PageInfoDto(endCursor, size);
    }
}
