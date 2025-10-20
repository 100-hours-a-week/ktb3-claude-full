package ktb.repository;

import java.util.Optional;
import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;

public interface ArticleRepository {
    Optional<Article> findById(Long id);

    Optional<Article> findByTitle(String title);

    Slice<Article> findAll(Long cursorId, int size);

    Optional<Long> getNextCursor(Long lastId);

    void like(Long id);

    void save (Article article);


    void deleteById(Long id);

    // ✅ 댓글
    ArticleComment addComment(Long articleId, String content, Long userId, String nickname);

    void updateComment(Long articleId, Long commentId, String newContent);

    void deleteComment(Long articleId, Long commentId);
}
