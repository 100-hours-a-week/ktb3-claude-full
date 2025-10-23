package ktb.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import ktb.constant.MessageConstant.CommentMessage;
import ktb.exception.article.AlreadyDeletedArticle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {
    private final Long id;
    private String title;
    private String content;
    private final Long createBy;
    private final ArticleMeta meta;
    private final AtomicLong commentSeq;
    private final ConcurrentLinkedDeque<ArticleComment> comments;
    private final String imagePath;
    private boolean isDeleted;
    private LocalDateTime deleteAt;

    public static Article create(Long id, String title, String content, Long userId, String imagePath) {
        return new Article(
                id, title, content, userId,
                ArticleMeta.init(id),
                new AtomicLong(0),
                new ConcurrentLinkedDeque<>(),
                imagePath,
                Boolean.FALSE,
                null
        );
    }

    public static Article create(Long id,Article article) {
        return new Article(
                id, article.title, article.content, article.createBy,
                ArticleMeta.init(id),
                new AtomicLong(0),
                new ConcurrentLinkedDeque<>(),
                article.imagePath,
                Boolean.FALSE,
                null
        );
    }

    public void softDelete() {
        if(!this.isDeleted) {
            isDeleted = Boolean.TRUE;
            deleteAt = LocalDateTime.now();

            return;
        }

        throw new AlreadyDeletedArticle();
    }

    public void softDeleteAllComment() {
        comments.stream()
                .filter(ArticleComment::isDelete)
                .forEach(ArticleComment::softDelete);
    }

    public void softDeleteComment(Long commentId) {
        comments.stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findAny()
                .ifPresent(ArticleComment::softDelete);
    }

    public void softRestore() {
        if (isDeleted) {
            this.isDeleted = Boolean.FALSE;
            this.deleteAt = null;

            comments.stream()
                    .filter(ArticleComment::isDelete)
                    .forEach(ArticleComment::softRestore);
        }
    }

    public boolean isDelete() {
        return (isDeleted == Boolean.TRUE) && (deleteAt != null);
    }

    // 댓글 추가
    public ArticleComment addComment(String content, Long userId, String nickname) {
        long newCommentId = commentSeq.incrementAndGet();
        ArticleComment comment = ArticleComment.init(id, newCommentId, content, userId, nickname);

        comments.addLast(comment);
        meta.incrementCommentCnt();

        return comment;
    }

    // 댓글 수정
    public void updateComment(Long contentId, String content) {
        ArticleComment comment = comments.stream()
                .filter(c -> c.getId().equals(contentId))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(CommentMessage.NON_EXIST));

        comment.update(content);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        boolean removed = comments.removeIf(c -> c.getId().equals(commentId));
        
        if (removed) {
            meta.decrementCommentCnt();
        }
    }

    // 좋아요 증가
    public void incrementLike() { meta.incrementLikeCnt(); }

    // 조회수 증가
    public void incrementView() { meta.incrementViewCnt(); }

    // 댓글 전체 반환
    public List<ArticleComment> getAllComments() {
        return new ArrayList<>(comments);
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
