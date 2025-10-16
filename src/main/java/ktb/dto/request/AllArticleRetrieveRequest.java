package ktb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "게시글 전체 조회 요청 구조")
public record AllArticleRetrieveRequest(
        @Schema(description = "조회할 게시글의 ID(해당 ID 초과 조회)", example = "0")
        @NotNull
        @Min(0)
        long after,

        @Schema(description = "조회할 게시글 개수", example = "10")
        @NotNull
        @Min(1)
        int limit
) {
}
