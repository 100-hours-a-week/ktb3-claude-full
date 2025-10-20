package ktb.dto;

import java.util.List;

import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.domain.ArticleComment;

public record ArticleDto(
         Long id,
         String title,
         String content,
         Long userId,
         List<ArticleComment> comments,
         String imagePath
) {
    public Article toEntity() { return Article.create(null, this.title, this.content, this.userId, this.imagePath); }

    public Article toEntity(Long id) {
        return Article.create(id, this.title, this.content, this.userId, this.imagePath);
    }

    public static ArticleDto of(Long id,String title, String content, String imagePath) {
        return new ArticleDto(id, title, content, null, null, imagePath);
    }

    public static ArticleDto of(String title, String content, String imagePath) {
        return new ArticleDto(null, title, content, null, null, imagePath);
    }

    public static ArticleDto of(String title, String content, Long userId, String imagePath) {
        return new ArticleDto(null, title, content, userId, null, imagePath);
    }

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
