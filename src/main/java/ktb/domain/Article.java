package ktb.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import java.util.Optional;
import ktb.exception.article.AlreadyDeletedArticle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    @ManyToOne
    private UserAccount createBy;
    @OneToOne
    private ArticleMeta meta;
    private String imagePath;
    private boolean isDeleted;
    private LocalDateTime deleteAt;

    public void softDelete() {
        if(!this.isDeleted) {
            isDeleted = Boolean.TRUE;
            deleteAt = LocalDateTime.now();

            return;
        }

        throw new AlreadyDeletedArticle();
    }

    public void softRestore() {
        if (isDeleted) {
            this.isDeleted = Boolean.FALSE;
            this.deleteAt = null;
        }
    }

    public boolean isDelete() {
        return (isDeleted == Boolean.TRUE) && (deleteAt != null);
    }

    public void update(String title, String content) {
        Optional.ofNullable(title)
                .filter(t -> !t.isEmpty())
                .ifPresent(t -> this.title = t);

        Optional.ofNullable(content)
                .filter(c -> !c.isEmpty())
                .ifPresent(c -> this.content = c);

        if (title != null || content != null) {
            meta.updateTimestamp();
        }
    }
}
