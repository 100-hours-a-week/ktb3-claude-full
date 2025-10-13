package ktb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfoDto {
    private final Long endCursor;
    private final int size;

    public static PageInfoDto of(Long endCursor, int size) {
        return new PageInfoDto(endCursor, size);
    }
}
