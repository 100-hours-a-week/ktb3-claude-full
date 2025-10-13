package ktb.dto.request;

import jakarta.validation.constraints.NotNull;

public record ArticlePatchRequest(
        @NotNull String title,
        @NotNull String content,
        String articleImagePath,
        @NotNull Long userId //Session 도입 시 삭제
) {
}
