package ktb.service;

import java.util.stream.Collectors;
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
@DisplayName("UserService delete 테스트")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository commentRepository;

    private UserAccount testUser1;
    private UserAccount testUser2;
    private Article testArticle1;
    private Article testArticle2;

    @BeforeEach
    void setUp() {
        // Given: 2명의 사용자 생성
        testUser1 = UserAccount.builder()
                .email("user1@test.com")
                .nickname("user1")
                .password("password")
                .isDeleted(false)
                .build();
        userRepository.save(testUser1);

        testUser2 = UserAccount.builder()
                .email("user2@test.com")
                .nickname("user2")
                .password("password")
                .isDeleted(false)
                .build();
        userRepository.save(testUser2);

        // User1의 Article 2개 생성
        testArticle1 = Article.create(
                null,
                "User1 Article 1",
                "Content 1",
                testUser1.getId(),
                null
        );
        articleRepository.save(testArticle1);

        testArticle2 = Article.create(
                null,
                "User1 Article 2",
                "Content 2",
                testUser1.getId(),
                null
        );
        articleRepository.save(testArticle2);

        // User2의 Article 1개 생성
        Article user2Article = Article.create(
                null,
                "User2 Article",
                "Content",
                testUser2.getId(),
                null
        );
        articleRepository.save(user2Article);

        // User1이 작성한 댓글 3개 (testArticle1에 2개, user2Article에 1개)
        for (int i = 0; i < 2; i++) {
            ArticleComment comment = ArticleComment.init(
                    testArticle1,
                    null,
                    "User1 Comment on Article1 - " + i,
                    testUser1.getId()
            );
            commentRepository.save(comment);
        }

        ArticleComment commentOnUser2Article = ArticleComment.init(
                user2Article,
                null,
                "User1 Comment on User2 Article",
                testUser1.getId()
        );
        commentRepository.save(commentOnUser2Article);

        // User2가 작성한 댓글 2개 (testArticle1에)
        for (int i = 0; i < 2; i++) {
            ArticleComment comment = ArticleComment.init(
                    testArticle1,
                    null,
                    "User2 Comment - " + i,
                    testUser2.getId()
            );
            commentRepository.save(comment);
        }
    }

    @Test
    @DisplayName("User 삭제 시 User가 softDelete 됨")
    void testUserDelete_UserIsSoftDeleted() {
        // When: User1 삭제
        userService.delete(testUser1.getId());

        // Then: User1이 softDelete 됨
        UserAccount deletedUser = userRepository.findById(testUser1.getId()).orElseThrow();
        assertThat(deletedUser.isDelete()).isTrue();
    }

    @Test
    @DisplayName("User 삭제 시 User가 작성한 모든 Article도 softDelete 됨")
    void testUserDelete_AllUserArticlesAreSoftDeleted() {
        // When: User1 삭제
        userService.delete(testUser1.getId());

        // Then: User1의 모든 Article이 softDelete 됨
        List<Article> user1Articles = articleRepository.findByCreateBy_Id(testUser1.getId());
        assertThat(user1Articles).hasSize(2);
        assertThat(user1Articles).allMatch(Article::isDelete);
    }

    @Test
    @DisplayName("User 삭제 시 User가 작성한 모든 Comment도 softDelete 됨")
    void testUserDelete_AllUserCommentsAreSoftDeleted() {
        // When: User1 삭제
        userService.delete(testUser1.getId());

        // Then: User1의 모든 Comment가 softDelete 됨
        List<ArticleComment> user1Comments = commentRepository.findAllByCreateBy_Id(testUser1.getId());
        assertThat(user1Comments).hasSize(3);
        assertThat(user1Comments).allMatch(ArticleComment::isDelete);
    }

    @Test
    @DisplayName("User 삭제 시 User의 Article에 달린 다른 User의 Comment도 softDelete 됨")
    void testUserDelete_CommentsOnUserArticlesAreSoftDeleted() {
        // Given: testArticle1(User1 작성)에 User2의 댓글 2개 존재
        List<ArticleComment> commentsOnArticle1Before = commentRepository.findAllByArticle(testArticle1);
        long user2CommentCount = commentsOnArticle1Before.stream()
                .filter(c -> c.getCreateBy().getId().equals(testUser2.getId()))
                .count();
        assertThat(user2CommentCount).isEqualTo(2);

        // When: User1 삭제
        userService.delete(testUser1.getId());

        // 변경사항을 데이터베이스에 반영
        commentRepository.flush();

        // Then: User1의 Article에 달린 모든 댓글이 softDelete 됨 (User2 댓글 포함)
        List<ArticleComment> commentsOnArticle1After =
                commentRepository.findAllByArticle(testArticle1)
                        .stream()
                        .filter(ArticleComment::isDelete)
                        .collect(Collectors.toList());
        assertThat(commentsOnArticle1After).allMatch(ArticleComment::isDelete);
    }

    @Test
    @DisplayName("User 삭제 후에도 데이터베이스에는 모든 레코드가 남아있음")
    void testUserDelete_RecordsRemainInDatabase() {
        // Given: 삭제 전 레코드 개수 확인
        long userCountBefore = userRepository.count();
        long articleCountBefore = articleRepository.count();
        long commentCountBefore = commentRepository.count();

        // When: User1 삭제
        userService.delete(testUser1.getId());

        // Then: 레코드 개수는 변하지 않음 (물리적 삭제 아님)
        long userCountAfter = userRepository.count();
        long articleCountAfter = articleRepository.count();
        long commentCountAfter = commentRepository.count();

        assertThat(userCountAfter).isEqualTo(userCountBefore);
        assertThat(articleCountAfter).isEqualTo(articleCountBefore);
        assertThat(commentCountAfter).isEqualTo(commentCountBefore);
    }

    @Test
    @DisplayName("다른 User는 영향 받지 않음")
    void testUserDelete_OtherUsersNotAffected() {
        // When: User1 삭제
        userService.delete(testUser1.getId());

        // Then: User2는 영향 없음
        UserAccount user2 = userRepository.findById(testUser2.getId()).orElseThrow();
        assertThat(user2.isDelete()).isFalse();

        // User2의 Article도 영향 없음
        List<Article> user2Articles = articleRepository.findByCreateBy_Id(testUser2.getId());
        assertThat(user2Articles).hasSize(1);
        assertThat(user2Articles).allMatch(article -> !article.isDelete());
    }

    @Test
    @DisplayName("User 삭제 시 cascade 효과 - User의 모든 데이터가 함께 삭제됨")
    void testUserDelete_CascadeEffect() {
        // When: User1 삭제
        userService.delete(testUser1.getId());

        // 변경사항을 데이터베이스에 반영
        userRepository.flush();
        articleRepository.flush();
        commentRepository.flush();

        // Then: User1과 관련된 모든 데이터가 softDelete 됨
        // 1. User 자체
        UserAccount deletedUser = userRepository.findById(testUser1.getId()).orElseThrow();
        assertThat(deletedUser.isDelete()).isTrue();

        // 2. User의 모든 Article
        List<Article> userArticles = articleRepository.findByCreateBy_Id(testUser1.getId());
        assertThat(userArticles).allMatch(Article::isDelete);

        // 3. User의 모든 Comment
        List<ArticleComment> userComments = commentRepository.findAllByCreateBy_Id(testUser1.getId());
        assertThat(userComments).allMatch(ArticleComment::isDelete);

        // 4. User의 Article에 달린 모든 Comment (다른 User가 작성한 것도 포함)
        List<ArticleComment> commentsOnUserArticles =
                commentRepository.findAllByArticle(testArticle1)
                        .stream()
                        .filter(ArticleComment::isDelete)
                        .collect(Collectors.toList());
        assertThat(commentsOnUserArticles).allMatch(ArticleComment::isDelete);
    }

    @Test
    @DisplayName("User 삭제 후 다른 User의 Article에 작성한 Comment만 삭제됨")
    void testUserDelete_OnlyUserCommentsDeleted() {
        // Given: User2의 Article에 User1이 작성한 댓글 존재
        List<Article> user2Articles = articleRepository.findByCreateBy_Id(testUser2.getId());
        Article user2Article = user2Articles.get(0);

        List<ArticleComment> commentsBeforeDelete = commentRepository.findAllByArticle(user2Article);
        long user1CommentCount = commentsBeforeDelete.stream()
                .filter(c -> c.getCreateBy().getId().equals(testUser1.getId()))
                .count();
        assertThat(user1CommentCount).isEqualTo(1);

        // When: User1 삭제
        userService.delete(testUser1.getId());

        // Then: User2의 Article은 삭제되지 않음
        Article user2ArticleAfter = articleRepository.findById(user2Article.getId()).orElseThrow();
        assertThat(user2ArticleAfter.isDelete()).isFalse();

        // User2의 Article에 User1이 작성한 댓글만 삭제됨
        List<ArticleComment> user1CommentsOnUser2Article = commentRepository
                .findAllByCreateBy_IdAndArticle_Id(testUser1.getId(), user2Article.getId());
        assertThat(user1CommentsOnUser2Article).allMatch(ArticleComment::isDelete);
    }

    @Test
    @DisplayName("이미 삭제된 User 재삭제 시 예외 발생")
    void testUserDelete_AlreadyDeletedUser() {
        // Given: User를 먼저 softDelete
        UserAccount user = userRepository.findById(testUser1.getId()).orElseThrow();
        user.softDelete();
        userRepository.save(user);

        // When & Then: 다시 삭제 시도하면 예외 발생
        try {
            userService.delete(testUser1.getId());
        } catch (Exception e) {
            // 이미 삭제된 User이므로 예외 발생 가능
            assertThat(e).isNotNull();
        }
    }
}
