package ktb.dto;

import ktb.domain.Article;
import ktb.domain.UserAccount;
import ktb.dto.request.ArticlePatchRequest;
import ktb.dto.request.ArticleRequest;

public record SaveArticleDto(
        Long id,
        String title,

        String content,

        String articleImagePath,

        Long userId
) {
    public static SaveArticleDto of(ArticleRequest request, Long userId) {
        return new SaveArticleDto(null, request.title(), request.content(), request.articleImagePath(), userId);
    }

    public static SaveArticleDto of(Long id, ArticlePatchRequest request, Long userId) {
        return new SaveArticleDto(id, request.title(), request.content(), request.articleImagePath(), userId);
    }

    public Article toEntity(UserAccount user) {
        return Article.create(this.id, this.title, this.content, user, this.articleImagePath);
    }
}
