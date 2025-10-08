package ktb.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AllArticleRetrieveRequest(
        @NotNull
        @Min(0)
        long after,

        @NotNull
        @Min(1)
        int limit
) {
}
