package ktb.fixture;

import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;

/**
 * ArticleComment 테스트 픽스처
 * 테스트에서 사용할 ArticleComment 객체를 쉽게 생성하기 위한 유틸리티 클래스
 *
 * <p>사용 예시:
 * <pre>
 * // 기본 댓글
 * ArticleComment comment = ArticleCommentFixture.createDefault(article);
 *
 * // 특정 사용자의 댓글
 * ArticleComment comment = ArticleCommentFixture.createByUser(article, user);
 *
 * // 내용과 사용자 ID를 지정한 댓글
 * ArticleComment comment = ArticleCommentFixture.create(article, "content", 2L);
 * </pre>
 */
public class ArticleCommentFixture {

    private static final String DEFAULT_CONTENT = "Test Comment";
    private static final Long DEFAULT_USER_ID = 1L;

    /**
     * 기본 ArticleComment 생성
     *
     * @param article 댓글이 달릴 게시글
     * @return ArticleComment
     */
    public static ArticleComment createDefault(Article article) {
        return ArticleComment.init(
                article,
                null,
                DEFAULT_CONTENT,
                DEFAULT_USER_ID
        );
    }

    /**
     * 내용과 사용자 ID를 지정한 ArticleComment 생성
     *
     * @param article 댓글이 달릴 게시글
     * @param content 댓글 내용
     * @param userId  작성자 ID
     * @return ArticleComment
     */
    public static ArticleComment create(Article article, String content, Long userId) {
        return ArticleComment.init(
                article,
                null,
                content,
                userId
        );
    }

    /**
     * ID, 내용, 사용자 ID를 지정한 ArticleComment 생성
     *
     * @param article   댓글이 달릴 게시글
     * @param commentId 댓글 ID
     * @param content   댓글 내용
     * @param userId    작성자 ID
     * @return ArticleComment
     */
    public static ArticleComment create(Article article, Long commentId, String content, Long userId) {
        return ArticleComment.init(
                article,
                commentId,
                content,
                userId
        );
    }

    /**
     * 특정 ID를 가진 ArticleComment 생성
     *
     * @param article   댓글이 달릴 게시글
     * @param commentId 댓글 ID
     * @return ArticleComment
     */
    public static ArticleComment createWithId(Article article, Long commentId) {
        return ArticleComment.init(
                article,
                commentId,
                DEFAULT_CONTENT,
                DEFAULT_USER_ID
        );
    }

    /**
     * 특정 사용자가 작성한 ArticleComment 생성
     *
     * @param article 댓글이 달릴 게시글
     * @param user    작성자
     * @return ArticleComment
     */
    public static ArticleComment createByUser(Article article, UserAccount user) {
        return ArticleComment.init(
                article,
                null,
                DEFAULT_CONTENT,
                user.getId()
        );
    }

    /**
     * 특정 사용자 ID로 ArticleComment 생성
     *
     * @param article 댓글이 달릴 게시글
     * @param userId  작성자 ID
     * @return ArticleComment
     */
    public static ArticleComment createByUserId(Article article, Long userId) {
        return ArticleComment.init(
                article,
                null,
                DEFAULT_CONTENT,
                userId
        );
    }

    /**
     * 특정 내용을 가진 ArticleComment 생성
     *
     * @param article 댓글이 달릴 게시글
     * @param content 댓글 내용
     * @return ArticleComment
     */
    public static ArticleComment createWithContent(Article article, String content) {
        return ArticleComment.init(
                article,
                null,
                content,
                DEFAULT_USER_ID
        );
    }

    /**
     * 삭제된 ArticleComment 생성
     *
     * @param article 댓글이 달릴 게시글
     * @return 삭제된 ArticleComment
     */
    public static ArticleComment createDeleted(Article article) {
        ArticleComment comment = ArticleComment.init(
                article,
                null,
                DEFAULT_CONTENT,
                DEFAULT_USER_ID
        );

        comment.softDelete();
        return comment;
    }

    /**
     * 여러 개의 ArticleComment 생성
     *
     * @param article 댓글이 달릴 게시글
     * @param count   생성할 댓글 개수
     * @return ArticleComment 배열
     */
    public static ArticleComment[] createMultiple(Article article, int count) {
        ArticleComment[] comments = new ArticleComment[count];
        for (int i = 0; i < count; i++) {
            comments[i] = ArticleComment.init(
                    article,
                    null,
                    DEFAULT_CONTENT + " - " + i,
                    DEFAULT_USER_ID
            );
        }
        return comments;
    }

    /**
     * 특정 사용자가 여러 개의 ArticleComment 생성
     *
     * @param article 댓글이 달릴 게시글
     * @param userId  작성자 ID
     * @param count   생성할 댓글 개수
     * @return ArticleComment 배열
     */
    public static ArticleComment[] createMultipleByUser(Article article, Long userId, int count) {
        ArticleComment[] comments = new ArticleComment[count];
        for (int i = 0; i < count; i++) {
            comments[i] = ArticleComment.init(
                    article,
                    null,
                    DEFAULT_CONTENT + " - " + i,
                    userId
            );
        }
        return comments;
    }
}
