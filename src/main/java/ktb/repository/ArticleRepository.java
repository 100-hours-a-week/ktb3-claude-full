package ktb.repository;

import java.util.List;
import java.util.Optional;

import ktb.domain.Article;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findById(Long id);

    @Query("""
        SELECT article FROM Article article
        LEFT JOIN FETCH article.createBy
        INNER JOIN FETCH article.meta
        LEFT JOIN FETCH article.comments comment
        LEFT JOIN FETCH comment.createBy
        WHERE article.id = :id
    """)
    Optional<Article> findDetail(@Param ("id") Long id);

    @EntityGraph(attributePaths = {"comments"}, type = EntityGraphType.FETCH)
    List<Article> findByCreateBy_Id(Long userId);

    @EntityGraph(attributePaths = {"meta", "comments"}, type = EntityGraphType.FETCH)
    List<Article> findAllByIdGreaterThanOrderByIdAsc(Long cursorId, Limit limit);

    @EntityGraph(attributePaths = {"meta", "comments"}, type = EntityGraphType.FETCH)
    List<Article> findAllByOrderByIdAsc(Limit limit);

    void deleteById(Long id);
}
