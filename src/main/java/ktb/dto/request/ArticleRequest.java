package ktb.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArticleRequest(
        @NotNull
        @Size(max = 26)
        String title,

        @NotNull
        String content,

        @Nullable
        String articleImagePath
) {
}
