package ktb.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 좋아요 토글 응답")
public record ArticleLikeResponse(
        @Schema(description = "좋아요 여부", example = "true")
        boolean liked
) {
    public static ArticleLikeResponse from(boolean liked) {
        return new ArticleLikeResponse(liked);
    }
}
