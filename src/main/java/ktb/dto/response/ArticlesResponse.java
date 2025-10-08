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
}
