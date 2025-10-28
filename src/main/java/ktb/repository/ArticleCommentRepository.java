package ktb.repository;

import java.util.List;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findAllByArticle(Article article);
}
