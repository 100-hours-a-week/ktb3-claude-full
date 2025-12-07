package ktb.dto;

import ktb.domain.Article;
import ktb.domain.UserAccount;
import ktb.dto.request.ArticlePatchRequest;
import ktb.dto.request.ArticleRequest;
import lombok.Builder;

@Builder
public record SaveArticleDto(
        Long id,
        String title,

        String content,

        String articleImagePath,

        Long userId
) {
    public static SaveArticleDto of(Long id, ArticlePatchRequest request, Long userId) {
        return new SaveArticleDto(id, request.title(), request.content(), request.articleImagePath(), userId);
    }

    public Article toEntity() {
        return Article.create(this.id, this.title, this.content, this.userId, this.articleImagePath);
    }
}
