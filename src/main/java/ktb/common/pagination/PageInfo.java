package ktb.common.pagination;

public record PageInfo(
        boolean hasNext,
        Long endCursor
) {
    public static PageInfo of(boolean hasNext, Long endCursor) {
        return new PageInfo(hasNext, endCursor);
    }
}
