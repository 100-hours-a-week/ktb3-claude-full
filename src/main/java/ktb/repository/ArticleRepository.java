package ktb.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ktb.domain.Article;
import ktb.domain.UserAccount;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findById(Long id);

    Collection<Article> findByCreateBy(UserAccount createBy);

    Optional<Article> findByTitle(String title);

    List<Article> findAllByIdGreaterThanOrderByIdAsc(Long cursorId, Limit limit);

    List<Article> findAllByOrderByIdAsc(Limit limit);


    void deleteById(Long id);
}
