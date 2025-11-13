package ktb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "article_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"article_id", "user_id", "type"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_id", nullable = false)
    private Long targetId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private LikeTargetType type;

    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;

    private ArticleLike(Long targetId, UserAccount user, LikeTargetType type) {
        this.targetId = targetId;
        this.user = user;
        this.type = type;
        this.isLike = true;
        this.likedAt = LocalDateTime.now();
    }

    public static ArticleLike ofArticle(Long articleId, Long userId) {
        UserAccount user = UserAccount.builder()
                .id(userId)
                .build();
        return new ArticleLike(articleId, user, LikeTargetType.ARTICLE);
    }

    public boolean isLike() {
        return isLike;
    }

    public void like() {
        this.isLike = true;
        this.likedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.isLike = false;
    }
}
