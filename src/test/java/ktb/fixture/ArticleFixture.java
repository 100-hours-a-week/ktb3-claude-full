package ktb.fixture;

import java.util.ArrayList;
import java.util.List;

import ktb.domain.Article;
import ktb.domain.ArticleMeta;
import ktb.domain.UserAccount;

/**
 * Article 테스트 픽스처
 * 테스트에서 사용할 Article 객체를 쉽게 생성하기 위한 유틸리티 클래스
 *
 * <p>사용 예시:
 * <pre>
 * // 기본 게시글 (userId=1L)
 * Article article = ArticleFixture.createDefault();
 *
 * // 특정 ID를 가진 게시글
 * Article article = ArticleFixture.createWithId(1L);
 *
 * // 특정 사용자의 게시글
 * Article article = ArticleFixture.createByUser(user);
 *
 * // 제목과 사용자 ID를 지정한 게시글
 * Article article = ArticleFixture.create("Title", 5L);
 * </pre>
 */
public class ArticleFixture {

    private static final String DEFAULT_TITLE = "Test Article";
    private static final String DEFAULT_CONTENT = "Test Content";
    private static final Long DEFAULT_USER_ID = 1L;
    private static final String DEFAULT_IMAGE_PATH = "/images/article-test.jpg";

    /**
     * 기본 Article 생성
     * ID는 null (JPA가 생성), userId는 1L
     */
    public static Article createDefault() {
        return Article.create(
                null,
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_USER_ID,
                null
        );
    }

    /**
     * 특정 ID를 가진 Article 생성
     *
     * @param id 게시글 ID
     * @return Article
     */
    public static Article createWithId(Long id) {
        return Article.create(
                id,
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_USER_ID,
                null
        );
    }

    /**
     * 제목과 사용자 ID를 지정한 Article 생성
     *
     * @param title  제목
     * @param userId 작성자 ID
     * @return Article
     */
    public static Article create(String title, Long userId) {
        return Article.create(
                null,
                title,
                DEFAULT_CONTENT,
                userId,
                null
        );
    }

    /**
     * ID, 제목, 사용자 ID를 지정한 Article 생성
     *
     * @param id     게시글 ID
     * @param title  제목
     * @param userId 작성자 ID
     * @return Article
     */
    public static Article create(Long id, String title, Long userId) {
        return Article.create(
                id,
                title,
                DEFAULT_CONTENT,
                userId,
                null
        );
    }

    /**
     * 제목, 내용, 사용자 ID를 지정한 Article 생성
     *
     * @param title   제목
     * @param content 내용
     * @param userId  작성자 ID
     * @return Article
     */
    public static Article create(String title, String content, Long userId) {
        return Article.create(
                null,
                title,
                content,
                userId,
                null
        );
    }

    /**
     * 특정 ID와 제목을 가진 Article 생성
     *
     * @param id    게시글 ID
     * @param title 제목
     * @return Article
     */
    public static Article createWithIdAndTitle(Long id, String title) {
        return Article.create(
                id,
                title,
                DEFAULT_CONTENT,
                DEFAULT_USER_ID,
                null
        );
    }

    /**
     * 특정 사용자가 작성한 Article 생성
     *
     * @param user 작성자
     * @return Article
     */
    public static Article createByUser(UserAccount user) {
        return Article.create(
                null,
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                user.getId(),
                null
        );
    }

    /**
     * 특정 사용자 ID로 Article 생성
     *
     * @param userId 작성자 ID
     * @return Article
     */
    public static Article createByUserId(Long userId) {
        return Article.create(
                null,
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                userId,
                null
        );
    }

    /**
     * 이미지가 있는 Article 생성
     *
     * @return Article
     */
    public static Article createWithImage() {
        return Article.create(
                null,
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_USER_ID,
                DEFAULT_IMAGE_PATH
        );
    }

    /**
     * 이미지 경로를 지정한 Article 생성
     *
     * @param userId    작성자 ID
     * @param imagePath 이미지 경로
     * @return Article
     */
    public static Article createWithImage(Long userId, String imagePath) {
        return Article.create(
                null,
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                userId,
                imagePath
        );
    }

    /**
     * 삭제된 Article 생성
     *
     * @return 삭제된 Article
     */
    public static Article createDeleted() {
        Article article = Article.create(
                null,
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_USER_ID,
                null
        );

        article.softDelete();
        return article;
    }

    /**
     * 특정 제목과 내용을 가진 Article 생성
     *
     * @param title   제목
     * @param content 내용
     * @return Article
     */
    public static Article createWithTitleAndContent(String title, String content) {
        return Article.create(
                null,
                title,
                content,
                DEFAULT_USER_ID,
                null
        );
    }

    /**
     * ArticleMeta만 필요한 경우 Article을 생성해 Meta를 반환
     *
     * @param id Article ID
     * @return ArticleMeta
     */
    public static ArticleMeta createMeta(Long id) {
        return createWithId(id).getMeta();
    }

    /**
     * 연속된 ID를 가진 Article 목록 생성
     *
     * @param startId 시작 ID
     * @param count   생성할 게시글 개수
     * @return Article 리스트
     */
    public static List<Article> createSequentialList(long startId, int count) {
        List<Article> articles = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            long id = startId + i;
            articles.add(Article.create(
                    id,
                    DEFAULT_TITLE + " - " + id,
                    DEFAULT_CONTENT + " - " + id,
                    DEFAULT_USER_ID,
                    null
            ));
        }

        return articles;
    }
}
