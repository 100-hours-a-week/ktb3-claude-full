package ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import ktb.domain.Article;

@Schema(description = "게시글 페이지 조회 시 각 게시글 별 응답 구조")
public record ArticleSimpleDto(
        @Schema(description = "게시글 ID", example = "1")
        @JsonProperty("id")
        Long id,

        @Schema(description = "게시글 제목", example = "게시글 제목")
        @JsonProperty("title")
        String title,

        @Schema(description = "좋아요 수", example = "0")
        @JsonProperty("like_cnt")
        int likeCnt,

        @Schema(description = "댓글 수", example = "0")
        @JsonProperty("comment_cnt")
        int commentCnt,

        @Schema(description = "조회 수", example = "1")
        @JsonProperty("view_cnt")
        int viewCnt
) {
    public static ArticleSimpleDto from(Article article) {
        return new ArticleSimpleDto(
                article.getId(),
                article.getTitle(),
                article.getMeta().getLikeCnt().get(),
                article.getMeta().getCommentCnt().get(),
                article.getMeta().getViewCnt().get()
        );
    }
}
