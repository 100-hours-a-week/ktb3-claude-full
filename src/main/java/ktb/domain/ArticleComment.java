package ktb.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleComment {
    private final Long commentSeq;
    private final Long articleSeq;
    private final String content;

    private final Long createBy;
    private final LocalDateTime createAt;
    private final LocalDateTime updateAt;
}
