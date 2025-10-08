package ktb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfoDto {
    private final boolean hasNextPage;
    private final Long endCursor;
}
