package ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ktb.domain.Article;

import java.util.List;

public record ArticleDetailDto(
        @JsonProperty("id")
        String id,

        @JsonProperty("title")
        String title,

        @JsonProperty("content")
        String content,

        @JsonProperty("like_cnt")
        String likeCnt,

        @JsonProperty("comment_cnt")
        String commentCnt,

        @JsonProperty("view_cnt")
        String viewCnt,

        @JsonProperty("comment")
        List<CommentDto> comment
) {
    public static ArticleDetailDto from(Article article) {
        List<CommentDto> commentDtoList = article.getAllComments().stream()
                .map(CommentDto::from)
                .toList();

        return new ArticleDetailDto(
                String.valueOf(article.getId()),
                article.getTitle(),
                article.getContent(),
                String.valueOf(article.getMeta().getLikeCnt().get()),
                String.valueOf(article.getMeta().getCommentCnt().get()),
                String.valueOf(article.getMeta().getViewCnt().get()),
                commentDtoList
        );
    }
}
