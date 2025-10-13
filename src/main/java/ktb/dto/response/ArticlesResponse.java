package ktb.dto.response;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import ktb.common.pagination.PageInfo;

public record ArticlesResponse<T>(
        @NotNull
        String message,

        Collection<T> data,

        @NotNull
        PageInfo pageInfo
) {
        public static <T> ArticlesResponse<T> of(String message, Collection<T> data, PageInfo pageInfo) {
                return new ArticlesResponse<>(message, data, pageInfo);
        }
}
