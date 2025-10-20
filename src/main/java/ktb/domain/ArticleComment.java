package ktb.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleComment {
    private final Long id;
    private final Long articleId;
    private String content;

    private final Long createBy;
    private final String createByNickname;
    private final LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static ArticleComment init(Long articleId, Long commentId, String content, Long userId, String nickname) {
        return new ArticleComment(
                commentId, articleId, content, userId, nickname, LocalDateTime.now(), null
        );
    }

    public void update(String newContent) {
        this.content = newContent;
        this.updateAt = LocalDateTime.now();
    }
}
