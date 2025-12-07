package ktb.repository;

import jakarta.persistence.EntityManager;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import ktb.fixture.ArticleCommentFixture;
import ktb.fixture.ArticleFixture;
import ktb.fixture.UserAccountFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ArticleCommentRepository EntityGraph 테스트")
class ArticleCommentRepositoryTest {

    @Autowired
    private ArticleCommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private UserAccount testUser1;
    private UserAccount testUser2;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        // Given: 테스트 데이터 생성
        testUser1 = userRepository.save(UserAccountFixture.create("user1@test.com", "user1"));
        testUser2 = userRepository.save(UserAccountFixture.create("user2@test.com", "user2"));

        // Article 생성
        testArticle = articleRepository.save(ArticleFixture.create("Test Article", "Content", testUser1.getId()));

        // 여러 사용자가 작성한 댓글 생성
        for (int i = 0; i < 3; i++) {
            ArticleComment comment = ArticleCommentFixture.create(testArticle, "Comment by user1 - " + i, testUser1.getId());
            commentRepository.save(comment);
        }

        for (int i = 0; i < 2; i++) {
            ArticleComment comment = ArticleCommentFixture.create(testArticle, "Comment by user2 - " + i, testUser2.getId());
            commentRepository.save(comment);
        }

        // 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findAllByArticle - createBy를 EntityGraph로 조회하여 N+1 방지")
    void 게시글별_댓글조회_작성자_동시조회() {
        // When: Article의 모든 댓글 조회
        List<ArticleComment> comments = commentRepository.findAllByArticle(testArticle);

        // Then: 댓글 조회 성공
        assertThat(comments).hasSize(5);

        // EntityGraph로 createBy가 fetch되어 추가 쿼리 없이 접근 가능
        comments.forEach(comment -> {
            assertThat(comment.getCreateBy()).isNotNull();
            assertThat(comment.getCreateBy().getNickname()).isIn("user1", "user2");
        });
    }

    @Test
    @DisplayName("findAllByCreateBy_Id - createBy와 article을 EntityGraph로 조회")
    void 사용자별_댓글조회시_게시글_연관조회() {
        // When: 특정 사용자의 모든 댓글 조회
        List<ArticleComment> comments = commentRepository.findAllByCreateBy_Id(testUser1.getId());

        // Then: user1의 댓글만 조회
        assertThat(comments).hasSize(3);

        // EntityGraph로 createBy와 article이 fetch됨
        comments.forEach(comment -> {
            assertThat(comment.getCreateBy()).isNotNull();
            assertThat(comment.getCreateBy().getId()).isEqualTo(testUser1.getId());
            assertThat(comment.getArticle()).isNotNull();
            assertThat(comment.getArticle().getTitle()).isEqualTo("Test Article");
        });
    }

    @Test
    @DisplayName("findAllByCreateBy_IdAndArticle_Id - 특정 사용자의 특정 게시글 댓글 조회")
    void 사용자와_게시글로_댓글조회() {
        // When: 특정 사용자의 특정 게시글 댓글 조회
        List<ArticleComment> comments = commentRepository.findAllByCreateBy_IdAndArticle_Id(
                testUser2.getId(), testArticle.getId());

        // Then: user2의 testArticle 댓글만 조회
        assertThat(comments).hasSize(2);

        // EntityGraph로 createBy와 article이 fetch됨
        comments.forEach(comment -> {
            assertThat(comment.getCreateBy()).isNotNull();
            assertThat(comment.getCreateBy().getId()).isEqualTo(testUser2.getId());
            assertThat(comment.getArticle()).isNotNull();
            assertThat(comment.getArticle().getId()).isEqualTo(testArticle.getId());
        });
    }

    @Test
    @DisplayName("softDelete된 사용자의 댓글도 조회 가능 (FK 유지)")
    void 삭제된_사용자댓글도_조회() {
        // Given: user1을 softDelete
        testUser1.softDelete();
        userRepository.save(testUser1);

        entityManager.flush();
        entityManager.clear();

        // When: Article의 모든 댓글 조회
        List<ArticleComment> comments = commentRepository.findAllByArticle(testArticle);

        // Then: 삭제된 사용자의 댓글도 조회됨 (FK 유지)
        assertThat(comments).hasSize(5);

        // 삭제된 사용자 확인
        long deletedUserCommentCount = comments.stream()
                .filter(comment -> comment.getCreateBy().isDelete())
                .count();
        assertThat(deletedUserCommentCount).isEqualTo(3);
    }

    @Test
    @DisplayName("softDelete된 게시글의 댓글도 조회 가능 (FK 유지)")
    void 삭제된_게시글댓글도_조회() {
        // Given: Article을 softDelete
        testArticle.softDelete();
        articleRepository.save(testArticle);

        entityManager.flush();
        entityManager.clear();

        // When: user1의 모든 댓글 조회
        List<ArticleComment> comments = commentRepository.findAllByCreateBy_Id(testUser1.getId());

        // Then: 삭제된 게시글의 댓글도 조회됨 (FK 유지)
        assertThat(comments).hasSize(3);
        comments.forEach(comment -> {
            assertThat(comment.getArticle()).isNotNull();
            assertThat(comment.getArticle().isDelete()).isTrue();
        });
    }

    @Test
    @DisplayName("findAllByArticleIn - 여러 Article의 댓글 일괄 조회")
    void 여러_게시글의_댓글_일괄조회() {
        // Given: 추가 Article 생성
        Article article2 = articleRepository.save(ArticleFixture.create("Second Article", "Content 2", testUser2.getId()));

        // article2에 댓글 추가
        for (int i = 0; i < 2; i++) {
            ArticleComment comment = ArticleCommentFixture.create(article2, "Comment on article2 - " + i, testUser1.getId());
            commentRepository.save(comment);
        }

        entityManager.flush();
        entityManager.clear();

        // When: 여러 Article의 댓글 일괄 조회
        List<ArticleComment> comments = commentRepository.findAllByArticleIn(
                List.of(testArticle, article2));

        // Then: 두 게시글의 댓글 모두 조회
        assertThat(comments).hasSize(7); // 5 + 2

        // EntityGraph로 createBy가 fetch됨
        comments.forEach(comment -> {
            assertThat(comment.getCreateBy()).isNotNull();
        });
    }
}
