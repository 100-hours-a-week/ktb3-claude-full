package ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ktb.domain.Article;

public record ArticleSimpleDto(
        @JsonProperty("id")
        Long id,

        @JsonProperty("title")
        String title,

        @JsonProperty("like_cnt")
        int likeCnt,

        @JsonProperty("comment_cnt")
        int commentCnt,

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
