package ktb.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import java.util.Optional;
import ktb.exception.article.AlreadyDeletedArticle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "article")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "create_by")
    private UserAccount createBy;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "meta_id")
    private ArticleMeta meta;

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private List<ArticleComment> comments = new LinkedList<>();

    private String title;

    private String content;

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

    public void refreshActiveComments() {
        if(Boolean.TRUE.equals(isDeleted)) {
            this.comments = Collections.emptyList();
        }

        this.comments = this.comments.stream()
                .filter(comment -> !comment.isDelete())
                .sorted(Comparator.comparing(ArticleComment::getId))
                .toList();
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

    public static Article create(Long id, String title, String content, Long userId, String imagePath) {
        UserAccount user = UserAccount.builder()
                .id(userId)
                .build();

        ArticleMeta meta = ArticleMeta.init();

        return new Article(
                id,
                user,
                meta,
                new LinkedList<>(),
                title,
                content,
                imagePath,
                false,
                null
        );
    }
}
