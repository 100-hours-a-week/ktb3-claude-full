package ktb.repository;

import java.util.List;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    @EntityGraph(attributePaths = {"createBy"})
    List<ArticleComment> findAllByArticle(Article article);

    @EntityGraph(attributePaths = {"createBy"})
    List<ArticleComment> findAllByArticleIn(List<Article> articles);

    @EntityGraph(attributePaths = {"createBy", "article"})
    List<ArticleComment> findAllByCreateBy_Id(Long userId);

    @EntityGraph(attributePaths = {"createBy", "article"})
    List<ArticleComment> findAllByCreateBy_IdAndArticle_Id(Long userId, Long articleId);
}
