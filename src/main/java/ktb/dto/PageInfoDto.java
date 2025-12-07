package ktb.dto;

public record PageInfoDto(
        Long endCursor,
        int size
) {
    public static PageInfoDto of(Long endCursor, int size) {
        if(endCursor == null) {
            endCursor = 0L;
        }
        return new PageInfoDto(endCursor, size);
    }
}
