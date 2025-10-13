package ktb.repository;

import java.util.Optional;
import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import ktb.dto.ArticleDto;

public interface ArticleRepository {
    Optional<Article> findById(Long id);

    Optional<Article> findByTitle(String title);

    Slice<Article> findAll(Long cursorId, int size);

    Optional<Long> getNextCursor(Long lastId);

    void like(Long id);

    void save(ArticleDto dto);
    void save (Article article);

    void update(Article originArticle, Article updateArticle);

    void deleteById(Long id);

    void updateContent(Long id, String newTitle, String newContent);

    // ✅ 댓글
    ArticleComment addComment(Long articleId, String content, UserAccount user);

    void updateComment(Long articleId, Long commentId, String newContent);

    void deleteComment(Long articleId, Long commentId);
}
