package ktb.domain;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleMeta {
    private final Long articleId;
    private AtomicInteger likeCnt;
    private AtomicInteger viewCnt;
    private AtomicInteger commentCnt;

    private final LocalDateTime createAt;
    private LocalDateTime updateAt;

    protected static ArticleMeta init(Long articleId) {
        return new ArticleMeta(
                articleId,
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
