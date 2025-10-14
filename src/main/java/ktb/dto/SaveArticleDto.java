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

        Long userId // Session 도입 시 제거 예정
) {
    public static SaveArticleDto of(ArticleRequest request) {
        return new SaveArticleDto(null, request.title(), request.content(), request.articleImagePath(), request.userId());
    }

    public static SaveArticleDto of(Long id, ArticlePatchRequest request) {
        return new SaveArticleDto(id, request.title(), request.content(), request.articleImagePath(), request.userId());
    }

    public Article toEntity(UserAccount user) {
        return Article.create(this.id, this.title, this.content, user, this.articleImagePath);
    }
}
