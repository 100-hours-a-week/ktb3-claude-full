package ktb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by")
    private UserAccount createBy;

    @Getter
    private String content;

    @Getter
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Getter
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    public static ArticleComment init(Article article, Long commentId, String content, Long userId) {
        return new ArticleComment(
                commentId,
                article,
                UserAccount.builder()
                        .id(userId)
                        .build(),
                content,
                null,
                null,
                Boolean.FALSE,
                null
        );
    }

    public void softDelete() {
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
