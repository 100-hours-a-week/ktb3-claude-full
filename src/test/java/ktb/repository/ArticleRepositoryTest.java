package ktb.repository;

import jakarta.persistence.EntityManager;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.ArticleMeta;
import ktb.domain.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Limit;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ArticleRepository EntityGraph 테스트")
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleMetaRepository articleMetaRepository;

    @Autowired
    private ArticleCommentRepository commentRepository;

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
                "Test Title",
                "Test Content",
                testUser.getId(),
                null
        );
        articleRepository.save(testArticle);

        // 댓글 생성
        for (int i = 0; i < 3; i++) {
            ArticleComment comment = ArticleComment.init(
                    testArticle,
                    null,
                    "Comment " + i,
                    testUser.getId()
            );
            commentRepository.save(comment);
        }

        // 영속성 컨텍스트 초기화 (쿼리 확인용)
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findDetail - EntityGraph로 모든 연관 엔티티 한 번에 조회")
    void testFindDetail_WithEntityGraph() {
        // When: findDetail 호출
        Optional<Article> result = articleRepository.findDetail(testArticle.getId());

        // Then: Article이 조회됨
        assertThat(result).isPresent();
        Article article = result.get();

        // 영속성 컨텍스트를 초기화하지 않고도 접근 가능 (이미 fetch됨)
        assertThat(article.getCreateBy()).isNotNull();
        assertThat(article.getCreateBy().getNickname()).isEqualTo("testUser");
        assertThat(article.getMeta()).isNotNull();
        assertThat(article.getComments()).hasSize(3);

        // 댓글의 작성자도 fetch되어야 함
        article.getComments().forEach(comment -> {
            assertThat(comment.getCreateBy()).isNotNull();
        });
    }

    @Test
    @DisplayName("findAllByOrderByIdAsc - meta와 comments를 EntityGraph로 조회")
    void testFindAllByOrderByIdAsc_WithEntityGraph() {
        // Given: 추가 Article 생성
        for (int i = 0; i < 2; i++) {
            Article article = Article.create(
                    null,
                    "Title " + i,
                    "Content " + i,
                    testUser.getId(),
                    null
            );
            articleRepository.save(article);
        }

        entityManager.flush();
        entityManager.clear();

        // When: pagination 조회
        List<Article> articles = articleRepository.findAllByOrderByIdAsc(Limit.of(10));

        // Then: 조회 성공
        assertThat(articles).isNotEmpty();

        // meta와 comments 접근 시 추가 쿼리 없이 조회 가능
        articles.forEach(article -> {
            assertThat(article.getMeta()).isNotNull();
            assertThat(article.getComments()).isNotNull();
        });
    }

    @Test
    @DisplayName("findAllByIdGreaterThanOrderByIdAsc - cursor 기반 pagination")
    void testFindAllByIdGreaterThan_WithEntityGraph() {
        // Given: 여러 Article 생성
        for (int i = 0; i < 5; i++) {
            Article article = Article.create(
                    null,
                    "Title " + i,
                    "Content " + i,
                    testUser.getId(),
                    null
            );
            articleRepository.save(article);
        }

        entityManager.flush();
        entityManager.clear();

        // When: cursor 이후 데이터 조회
        Long cursorId = testArticle.getId();
        List<Article> articles = articleRepository.findAllByIdGreaterThanOrderByIdAsc(
                cursorId, Limit.of(3));

        // Then: cursor 이후 데이터만 조회
        assertThat(articles).isNotEmpty();
        assertThat(articles).allMatch(article -> article.getId() > cursorId);

        // EntityGraph로 fetch된 데이터 검증
        articles.forEach(article -> {
            assertThat(article.getMeta()).isNotNull();
            assertThat(article.getComments()).isNotNull();
        });
    }

    @Test
    @DisplayName("findByCreateBy_Id - softDelete된 사용자의 게시글도 조회 가능")
    void testFindByCreateBy_Id_WithSoftDeletedUser() {
        // Given: 사용자를 softDelete
        testUser.softDelete();
        userRepository.save(testUser);

        entityManager.flush();
        entityManager.clear();

        // When: 삭제된 사용자의 게시글 조회
        List<Article> articles = articleRepository.findByCreateBy_Id(testUser.getId());

        // Then: FK가 유지되므로 조회 가능
        assertThat(articles).isNotEmpty();
        assertThat(articles.get(0).getCreateBy().isDelete()).isTrue();
    }

    @Test
    @DisplayName("findDetail - softDelete된 사용자도 LEFT JOIN으로 조회 가능")
    void testFindDetail_WithSoftDeletedUser() {
        // Given: 사용자를 softDelete
        testUser.softDelete();
        userRepository.save(testUser);

        entityManager.flush();
        entityManager.clear();

        // When: LEFT JOIN으로 조회
        Optional<Article> result = articleRepository.findDetail(testArticle.getId());

        // Then: Article 조회 성공, createBy도 존재
        assertThat(result).isPresent();
        Article article = result.get();
        assertThat(article.getCreateBy()).isNotNull();
        assertThat(article.getCreateBy().isDelete()).isTrue();
    }
}
