package ktb.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_meta")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OneToOne
    private Article article;
    private AtomicInteger likeCnt;
    private AtomicInteger viewCnt;
    private AtomicInteger commentCnt;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    protected static ArticleMeta init(Article article) {
        return new ArticleMeta(
                article,
                new AtomicInteger(0),
                new AtomicInteger(0),
                new AtomicInteger(0),
                LocalDateTime.now() ,
                null);
    }

    protected void incrementViewCnt() { viewCnt.incrementAndGet(); }
    protected void incrementLikeCnt() { likeCnt.incrementAndGet(); }
    protected void incrementCommentCnt() { commentCnt.incrementAndGet(); }
    protected void decrementCommentCnt() { commentCnt.decrementAndGet(); }

    protected void updateTimestamp() {
        this.updateAt = LocalDateTime.now();
    }
}
