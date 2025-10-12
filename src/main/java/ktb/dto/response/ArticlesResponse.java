package ktb.dto.response;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import ktb.dto.PageInfoDto;

public record ArticlesResponse<T>(
        @NotNull
        String message,

        Collection<T> data,

        @NotNull
        PageInfoDto pageInfo
) {
        public static <T> ArticlesResponse<T> of(String message, Collection<T> data, PageInfoDto pageInfo) {
                return new ArticlesResponse<>(message, data, pageInfo);
        }
}
