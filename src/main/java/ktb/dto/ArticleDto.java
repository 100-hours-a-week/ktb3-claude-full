package ktb.dto;

import java.util.List;

import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleDto {
    private final Long id;
    private final String title;
    private final String content;
    private final UserAccount user;
    private final List<ArticleComment> comments;
    private final String imagePath;
    public Article toEntity() { return Article.create(null, this.title, this.content, this.user, this.imagePath); }

    public Article toEntity(Long id) {
        return Article.create(id, this.title, this.content, this.user, this.imagePath);
    }

    public static ArticleDto of(Long id,String title, String content, String imagePath) {
        return new ArticleDto(id, title, content, null, null, imagePath);
    }

    public static ArticleDto of(String title, String content, String imagePath) {
        return new ArticleDto(null, title, content, null, null, imagePath);
    }

    public static ArticleDto of(String title, String content, UserAccount user, String imagePath) {
        return new ArticleDto(null, title, content, user, null, imagePath);
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
