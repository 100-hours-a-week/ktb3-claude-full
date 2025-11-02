package ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import ktb.domain.Article;

import java.util.List;

@Schema(description = "게시글 상세 조회 응답 구조")
public record ArticleDetailDto(
        @Schema(description = "게시글 ID", example = "1")
        @JsonProperty("id")
        String id,

        @Schema(description = "게시글 제목", example = "게시글 제목")
        @JsonProperty("title")
        String title,

        @Schema(description = "게시글 내용", example = "게시글 내용")
        @JsonProperty("content")
        String content,

        @Schema(description = "좋아요 수", example = "0")
        @JsonProperty("like_cnt")
        String likeCnt,

        @Schema(description = "댓글 수", example = "0")
        @JsonProperty("comment_cnt")
        String commentCnt,

        @Schema(description = "조회 수", example = "1")
        @JsonProperty("view_cnt")
        String viewCnt,

        @Schema(description = "댓글 목록", implementation = CommentDetailDto.class)
        @JsonProperty("comment")
        List<CommentDetailDto> comment
) {
    public static ArticleDetailDto from(Article article) {
        return new ArticleDetailDto(
                String.valueOf(article.getId()),
                article.getTitle(),
                article.getContent(),
                String.valueOf(article.getMeta().getLikeCnt().get()),
                String.valueOf(article.getComments().size()),
                String.valueOf(article.getMeta().getViewCnt().get()),
                article.getComments().stream()
                        .map(CommentDetailDto::from)
                        .toList()
        );
    }
}
