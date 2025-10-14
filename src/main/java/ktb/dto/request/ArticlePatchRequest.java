package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record ArticlePatchRequest(
        @NotNull String title,
        @NotNull String content,
        String articleImagePath,
        @NotNull
        @JsonProperty("user_id")
        Long userId //Session 도입 시 삭제
) {
}
