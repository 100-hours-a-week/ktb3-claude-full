package ktb.repository;

import java.util.Optional;
import ktb.domain.ArticleLike;
import ktb.domain.LikeTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByTargetIdAndUserIdAndType(Long targetId, Long userId, LikeTargetType type);
    boolean existsByTargetIdAndUserIdAndTypeAndIsLikeTrue(Long targetId, Long userId, LikeTargetType type);
}
