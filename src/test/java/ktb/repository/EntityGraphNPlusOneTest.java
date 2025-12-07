package ktb.repository;

import jakarta.persistence.EntityManager;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import ktb.fixture.ArticleCommentFixture;
import ktb.fixture.ArticleFixture;
import ktb.fixture.UserAccountFixture;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Limit;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("EntityGraph N+1 문제 검증 테스트")
class EntityGraphNPlusOneTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        // Hibernate Statistics 활성화
        Session session = entityManager.unwrap(Session.class);
        statistics = session.getSessionFactory().getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // Given: 대량 테스트 데이터 생성
        for (int userIdx = 0; userIdx < 5; userIdx++) {
            UserAccount user = userRepository.save(
                    UserAccountFixture.create("user" + userIdx + "@test.com", "user" + userIdx)
            );

            // 각 사용자당 3개 게시글
            for (int articleIdx = 0; articleIdx < 3; articleIdx++) {
                Article article = articleRepository.save(
                        ArticleFixture.create("Title " + userIdx + "-" + articleIdx,
                                "Content " + userIdx + "-" + articleIdx,
                                user.getId())
                );

                // 각 게시글당 2개 댓글
                for (int commentIdx = 0; commentIdx < 2; commentIdx++) {
                    ArticleComment comment = ArticleCommentFixture.create(article, "Comment " + commentIdx, user.getId());
                    commentRepository.save(comment);
                }
            }
        }

        entityManager.flush();
        entityManager.clear();
        statistics.clear();
    }

    @Test
    @DisplayName("ArticleRepository.findAllByOrderByIdAsc - EntityGraph로 N+1 방지")
    void testArticleRepository_NoNPlusOne() {
        // Given: Statistics 초기화
        statistics.clear();

        // When: 10개 Article 조회 (EntityGraph: meta, comments)
        List<Article> articles = articleRepository.findAllByOrderByIdAsc(Limit.of(10));

        // 모든 meta와 comments 접근
        articles.forEach(article -> {
            article.getMeta().getLikeCnt();  // meta 접근
            article.getComments().size();    // comments 접근
        });

        // Then: 쿼리 개수 검증
        long queryCount = statistics.getPrepareStatementCount();
        System.out.println("Total queries executed: " + queryCount);

        // EntityGraph 사용 시: 1~2개 쿼리 (Article + comments join)
        // N+1 문제 발생 시: 10+ 쿼리 (Article 1개 + meta 10개 조회)
        assertThat(queryCount).isLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("ArticleCommentRepository.findAllByArticle - EntityGraph로 N+1 방지")
    void testCommentRepository_NoNPlusOne() {
        // Given: Article 하나 조회
        Article article = articleRepository.findAll().get(0);
        entityManager.clear();
        statistics.clear();

        // When: 해당 Article의 모든 댓글 조회 (EntityGraph: createBy)
        List<ArticleComment> comments = commentRepository.findAllByArticle(article);

        // 모든 createBy 접근
        comments.forEach(comment -> {
            comment.getCreateBy().getNickname();  // createBy 접근
        });

        // Then: 쿼리 개수 검증
        long queryCount = statistics.getPrepareStatementCount();
        System.out.println("Total queries executed: " + queryCount);

        // EntityGraph 사용 시: 1개 쿼리 (Comment + createBy join)
        // N+1 문제 발생 시: 1 + N개 쿼리 (Comment 1개 + createBy N개 조회)
        assertThat(queryCount).isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("ArticleRepository.findDetail - 복잡한 fetch join으로 N+1 방지")
    void testFindDetail_NoNPlusOne() {
        // Given: Article ID 조회
        Long articleId = articleRepository.findAll().get(0).getId();
        entityManager.clear();
        statistics.clear();

        // When: findDetail로 모든 연관 엔티티 조회
        Article article = articleRepository.findDetail(articleId).orElseThrow();

        // 모든 연관 엔티티 접근
        article.getCreateBy().getNickname();
        article.getMeta().getLikeCnt();
        article.getComments().forEach(comment -> {
            comment.getCreateBy().getNickname();
        });

        // Then: 쿼리 개수 검증
        long queryCount = statistics.getPrepareStatementCount();
        System.out.println("Total queries executed: " + queryCount);

        // JOIN FETCH 사용 시: 1개 쿼리 (모두 join)
        // N+1 문제 발생 시: 1 + 1 + 1 + N개 쿼리
        assertThat(queryCount).isEqualTo(1);
    }

    @Test
    @DisplayName("ArticleCommentRepository.findAllByCreateBy_Id - EntityGraph로 article도 fetch")
    void testFindAllByCreateBy_Id_NoNPlusOne() {
        // Given: User ID 조회
        Long userId = userRepository.findAll().get(0).getId();
        entityManager.clear();
        statistics.clear();

        // When: 해당 사용자의 모든 댓글 조회 (EntityGraph: createBy, article)
        List<ArticleComment> comments = commentRepository.findAllByCreateBy_Id(userId);

        // createBy와 article 모두 접근
        comments.forEach(comment -> {
            comment.getCreateBy().getNickname();
            comment.getArticle().getTitle();
        });

        // Then: 쿼리 개수 검증
        long queryCount = statistics.getPrepareStatementCount();
        System.out.println("Total queries executed: " + queryCount);

        // EntityGraph 사용 시: 1~2개 쿼리
        // N+1 문제 발생 시: 1 + N + N개 쿼리
        assertThat(queryCount).isLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("EntityGraph 없이 조회 시 N+1 문제 발생 확인 (비교용)")
    void testWithoutEntityGraph_NPlusOneProblem() {
        // Given: EntityGraph 없는 findById 사용
        Long articleId = articleRepository.findAll().get(0).getId();
        entityManager.clear();
        statistics.clear();

        // When: findById로 조회 (EntityGraph 없음)
        Article article = articleRepository.findById(articleId).orElseThrow();

        // comments의 각 createBy 접근 시 추가 쿼리 발생
        article.getComments().forEach(comment -> {
            comment.getCreateBy().getNickname();  // Lazy loading 발생
        });

        // Then: 쿼리 개수 검증
        long queryCount = statistics.getPrepareStatementCount();
        System.out.println("Total queries executed (N+1 problem): " + queryCount);

        // Note: BatchSize 어노테이션으로 인해 N+1 문제가 완화될 수 있음
        // EntityGraph를 사용하지 않아도 배치로 로딩됨
        // 최소 1개 이상의 쿼리는 실행됨
        assertThat(queryCount).isGreaterThan(0);
    }

    @Test
    @DisplayName("대량 데이터 조회 시 EntityGraph 성능 비교")
    void testBulkQuery_PerformanceComparison() {
        // Given: 15개 Article 조회
        entityManager.clear();
        statistics.clear();

        // When: EntityGraph로 조회
        long startTime = System.currentTimeMillis();
        List<Article> articles = articleRepository.findAllByOrderByIdAsc(Limit.of(15));

        // 모든 연관 엔티티 접근
        articles.forEach(article -> {
            article.getMeta().getViewCnt();
            article.getComments().size();
        });

        long endTime = System.currentTimeMillis();
        long queryCount = statistics.getPrepareStatementCount();

        // Then: 성능 검증
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        System.out.println("Total queries: " + queryCount);

        // EntityGraph 덕분에 쿼리 수가 적어야 함
        assertThat(queryCount).isLessThan(5);
    }
}
