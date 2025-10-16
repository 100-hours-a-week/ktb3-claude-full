package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "게시글 수정 요청 구조")
public record ArticlePatchRequest(
        @Schema(description = "게시글 제목", example = "게시글 제목")
        @NotNull
        String title,

        @Schema(description = "게시글 내용", example = "게시글 내용")
        @NotNull String content,

        @Schema(description = "게시글에 첨부할 이미지 경로", example = "https://image.com/image.png")
        String articleImagePath,

        @Schema(description = "수정 시도한 유저 ID(Sequential ID)", example = "1")
        @NotNull
        @JsonProperty("user_id")
        Long userId //Session 도입 시 삭제
) {
}
