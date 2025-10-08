package ktb.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleMeta {
    private final Long articleSeq;
    private final int likeCnt;
    private final int viewCnt;
    private final int commentCnt;

    private final LocalDateTime createAt;
    private final LocalDateTime updateAt;
}
