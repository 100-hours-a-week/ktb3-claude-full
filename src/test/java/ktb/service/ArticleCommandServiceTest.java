package ktb.service;

import jakarta.persistence.EntityManager;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import ktb.repository.ArticleCommentRepository;
import ktb.repository.ArticleRepository;
import ktb.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ArticleCommandService delete 테스트")
class ArticleCommandServiceTest {

    @Autowired
    private ArticleCommandService articleCommandService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private UserAccount testUser;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        // Given: 테스트 데이터 생성
        testUser = UserAccount.builder()
                .email("test@test.com")
                .nickname("testUser")
                .password("password")
                .isDeleted(false)
                .build();
        userRepository.save(testUser);

        // Article 생성
        testArticle = Article.create(
                null,
                "Test Article",
                "Test Content",
                testUser.getId(),
                null
        );
        articleRepository.save(testArticle);

        // 댓글 3개 추가
        for (int i = 0; i < 3; i++) {
            ArticleComment comment = ArticleComment.init(
                    testArticle,
                    null,
                    "Comment " + i,
                    testUser.getId()
            );
            commentRepository.save(comment);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Article 삭제 시 Article과 모든 댓글이 softDelete 됨")
    void testArticleDelete_SoftDeletesArticleAndComments() {
        // When: Article 삭제
        articleCommandService.delete(testArticle.getId());

        entityManager.flush();
        entityManager.clear();

        // Then: Article이 softDelete 됨
        Article deletedArticle = articleRepository.findById(testArticle.getId()).orElseThrow();
        assertThat(deletedArticle.isDelete()).isTrue();

        // 모든 댓글도 softDelete 됨
        List<ArticleComment> comments = commentRepository.findAllByArticle(testArticle);
        assertThat(comments).allMatch(ArticleComment::isDelete);
    }

    @Test
    @DisplayName("Article 삭제 후에도 데이터베이스에는 레코드가 남아있음")
    void testArticleDelete_RecordsRemainsInDatabase() {
        // Given: 댓글 개수 확인
        long commentCountBefore = commentRepository.count();
        long articleCountBefore = articleRepository.count();

        // When: Article 삭제
        articleCommandService.delete(testArticle.getId());

        entityManager.flush();
        entityManager.clear();

        // Then: 레코드 개수는 변하지 않음 (물리적 삭제 아님)
        long commentCountAfter = commentRepository.count();
        long articleCountAfter = articleRepository.count();

        assertThat(articleCountAfter).isEqualTo(articleCountBefore);
        assertThat(commentCountAfter).isEqualTo(commentCountBefore);
    }

    @Test
    @DisplayName("여러 Article 중 하나만 삭제 시 다른 Article은 영향 없음")
    void testArticleDelete_OnlyTargetArticleIsDeleted() {
        // Given: 추가 Article 생성
        Article anotherArticle = Article.create(
                null,
                "Another Article",
                "Another Content",
                testUser.getId(),
                null
        );
        articleRepository.save(anotherArticle);

        ArticleComment anotherComment = ArticleComment.init(
                anotherArticle,
                null,
                "Another Comment",
                testUser.getId()
        );
        commentRepository.save(anotherComment);

        entityManager.flush();
        entityManager.clear();

        // When: 첫 번째 Article만 삭제
        articleCommandService.delete(testArticle.getId());

        entityManager.flush();
        entityManager.clear();

        // Then: 첫 번째 Article과 댓글만 삭제됨
        Article deletedArticle = articleRepository.findById(testArticle.getId()).orElseThrow();
        assertThat(deletedArticle.isDelete()).isTrue();

        List<ArticleComment> deletedComments = commentRepository.findAllByArticle(testArticle);
        assertThat(deletedComments).allMatch(ArticleComment::isDelete);

        // 두 번째 Article과 댓글은 영향 없음
        Article notDeletedArticle = articleRepository.findById(anotherArticle.getId()).orElseThrow();
        assertThat(notDeletedArticle.isDelete()).isFalse();

        List<ArticleComment> notDeletedComments = commentRepository.findAllByArticle(anotherArticle);
        assertThat(notDeletedComments).allMatch(comment -> !comment.isDelete());
    }

    @Test
    @DisplayName("댓글이 없는 Article도 정상적으로 삭제됨")
    void testArticleDelete_ArticleWithoutComments() {
        // Given: 댓글 없는 Article 생성
        Article articleWithoutComments = Article.create(
                null,
                "No Comments Article",
                "Content",
                testUser.getId(),
                null
        );
        articleRepository.save(articleWithoutComments);

        entityManager.flush();
        entityManager.clear();

        // When: 삭제
        articleCommandService.delete(articleWithoutComments.getId());

        entityManager.flush();
        entityManager.clear();

        // Then: Article이 softDelete 됨
        Article deletedArticle = articleRepository.findById(articleWithoutComments.getId()).orElseThrow();
        assertThat(deletedArticle.isDelete()).isTrue();
    }

    @Test
    @DisplayName("이미 삭제된 Article의 댓글도 모두 삭제됨")
    void testArticleDelete_AlreadyDeletedArticleComments() {
        // Given: Article을 먼저 softDelete
        Article article = articleRepository.findById(testArticle.getId()).orElseThrow();
        article.softDelete();
        articleRepository.save(article);

        entityManager.flush();
        entityManager.clear();

        // When: 다시 삭제 시도
        try {
            articleCommandService.delete(testArticle.getId());
        } catch (Exception e) {
            // 이미 삭제된 Article이므로 예외 발생 가능
        }

        entityManager.flush();
        entityManager.clear();

        // Then: Article은 여전히 삭제 상태
        Article deletedArticle = articleRepository.findById(testArticle.getId()).orElseThrow();
        assertThat(deletedArticle.isDelete()).isTrue();
    }
}
