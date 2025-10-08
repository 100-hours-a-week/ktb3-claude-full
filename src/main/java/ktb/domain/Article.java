package ktb.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Article {
    private final Long articleSeq;
    private final String title;
    private final String content;
    private final List<Long> comments;
    private final Long createBy;
}
