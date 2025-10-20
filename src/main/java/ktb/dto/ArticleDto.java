package ktb.dto;

import java.util.List;

import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import lombok.Builder;

@Builder
public record ArticleDto(
         Long id,
         String title,
         String content,
         Long userId,
         List<ArticleComment> comments,
         String imagePath
) {
    public static ArticleDto from(Article article) {
        return new ArticleDto(article.getId(), article.getTitle(), article.getContent()
                , article.getCreateBy(), article.getAllComments(), article.getImagePath());
    }

    public static Slice<ArticleDto> from(Slice<Article> article) {
        List<ArticleDto> dtoList = article.content().stream()
                .map(ArticleDto::from)
                .toList();

        return Slice.of(
                dtoList,
                article.hasNext(),
                article.nextCursor()
        );
    }
}
