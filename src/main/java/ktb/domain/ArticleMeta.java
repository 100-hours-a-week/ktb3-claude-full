package ktb.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long id;
    private AtomicInteger likeCnt;
    private AtomicInteger viewCnt;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    protected static ArticleMeta init() {
        return new ArticleMeta(
                null,
                new AtomicInteger(0),
                new AtomicInteger(0),
                LocalDateTime.now() ,
                null);
    }

    public void increaseLikeCnt() {
        likeCnt.incrementAndGet();
    }

    public void decreaseLikeCnt() {
        likeCnt.updateAndGet(current -> Math.max(0, current - 1));
    }

    protected void updateTimestamp() {
        this.updateAt = LocalDateTime.now();
    }
}
