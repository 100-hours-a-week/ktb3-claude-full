package ktb.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import ktb.exception.article.AlreadyDeletedComment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_comment")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    @Getter private Article article;
    @Getter private String content;

    @Getter private Long createBy;
    @Getter private String createByNickname;
    @Getter private LocalDateTime createAt;
    @Getter private LocalDateTime updateAt;
    private boolean isDeleted;
    private LocalDateTime deleteAt;

    public static ArticleComment init(Article article, Long commentId, String content, Long userId, String nickname) {
        return new ArticleComment(
                commentId,
                article,
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
