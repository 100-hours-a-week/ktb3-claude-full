package ktb.domain;

import java.time.LocalDateTime;
import ktb.exception.article.AlreadyDeletedComment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleComment {
    @Getter private final Long id;
    @Getter private final Long articleId;
    @Getter private String content;

    @Getter private final Long createBy;
    @Getter private final String createByNickname;
    @Getter private final LocalDateTime createAt;
    @Getter private LocalDateTime updateAt;
    private boolean isDeleted;
    private LocalDateTime deleteAt;

    public static ArticleComment init(Long articleId, Long commentId, String content, Long userId, String nickname) {
        return new ArticleComment(
                commentId,
                articleId,
                content,
                userId,
                nickname,
                LocalDateTime.now(),
                null,
                Boolean.FALSE,
                null
        );
    }

    protected void softDelete() {
        if(!isDeleted) {
            isDeleted = Boolean.TRUE;

            deleteAt = LocalDateTime.now();

            return;
        }

        throw new AlreadyDeletedComment();
    }

    protected void softRestore() {
        if (isDeleted) {
            isDeleted = Boolean.FALSE;
            deleteAt = null;
        }
    }

    public boolean isDelete() {
        return (isDeleted == Boolean.TRUE) && (deleteAt != null);
    }

    public void update(String newContent) {
        this.content = newContent;
        this.updateAt = LocalDateTime.now();
    }
}
