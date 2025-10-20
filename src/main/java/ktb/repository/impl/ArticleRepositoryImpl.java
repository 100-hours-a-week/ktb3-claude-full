package ktb.repository.impl;

import java.util.Optional;
import ktb.common.pagination.Slice;
import ktb.db.article.ArticleData;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import ktb.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

    @Override
    public Optional<Article> findById(Long id) {
        return ArticleData.findById(id);
    }

    @Override
    public Optional<Article> findByTitle(String title) {
        return ArticleData.findByTitle(title);
    }

    @Override
    public Slice<Article> findAll(Long cursorId, int size) {
        return ArticleData.findAll(cursorId, size);
    }

    @Override
    public Optional<Long> getNextCursor(Long lastId) {
        return ArticleData.getNextCursor(lastId);
    }

    @Override
    public void like(Long id) {
        ArticleData.like(id);
    }


    @Override
    public void save(Article article) {
        ArticleData.save(article);
    }

    @Override
    public void deleteById(Long id) {
        ArticleData.deleteById(id);
    }

    @Override
    public ArticleComment addComment(Long articleId, String content, Long userId, String nickname) {
        return ArticleData.addComment(articleId, content, userId, nickname);
    }

    @Override
    public void updateComment(Long articleId, Long commentId, String newContent) {
        ArticleData.updateComment(articleId, commentId, newContent);
    }

    @Override
    public void deleteComment(Long articleId, Long commentId) {
        ArticleData.deleteComment(articleId, commentId);
    }
}
