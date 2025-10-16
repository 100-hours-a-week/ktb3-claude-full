package ktb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "게시글 던건 등록 요청 구조")
public record ArticleRequest(
        @Schema(description = "게시글 제목", example = "게시글 제목")
        @NotNull
        @Size(max = 26)
        String title,

        @NotNull
        @Schema(description = "게시글 내용", example = "게시글 내용")
        String content,

        @Schema(description = "게시글에 첨부할 이미지 경로", example = "https://image.com/image.png")
        @Nullable
        String articleImagePath,

        @Schema(description = "등록 시도한 유저 ID(Sequential ID)", example = "1")
        @NotNull
        Long userId // Session 도입 시 제거 예정
) {
}
