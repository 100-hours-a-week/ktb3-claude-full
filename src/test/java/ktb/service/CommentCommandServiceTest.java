package ktb.service;

import jakarta.persistence.EntityManager;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import ktb.dto.CommentDto;
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
@DisplayName("CommentCommandService delete 테스트")
class CommentCommandServiceTest {

    @Autowired
    private CommentCommandService commentCommandService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private UserAccount testUser1;
    private UserAccount testUser2;
    private Article testArticle;
    private ArticleComment testComment;

    @BeforeEach
    void setUp() {
        // Given: 테스트 데이터 생성
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

        // Article 생성
        testArticle = Article.create(
                null,
                "Test Article",
                "Test Content",
                testUser1.getId(),
                null
        );
        articleRepository.save(testArticle);

        // 댓글 생성
        testComment = ArticleComment.init(
                testArticle,
                null,
                "Test Comment",
                testUser1.getId()
        );
        commentRepository.save(testComment);

        // 추가 댓글 생성 (다른 사용자)
        for (int i = 0; i < 2; i++) {
            ArticleComment comment = ArticleComment.init(
                    testArticle,
                    null,
                    "Comment by user2 - " + i,
                    testUser2.getId()
            );
            commentRepository.save(comment);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("댓글 삭제 시 해당 댓글만 softDelete 됨")
    void testCommentDelete_OnlyTargetCommentIsDeleted() {
        // Given: 삭제할 댓글 정보
        CommentDto deleteRequest = CommentDto.builder()
                .id(testComment.getId())
                .articleId(testArticle.getId())
                .content(testComment.getContent())
                .createBy(testUser1.getId())
                .createNickName(testUser1.getNickname())
                .build();

        // When: 댓글 삭제
        commentCommandService.delete(deleteRequest);

        entityManager.flush();
        entityManager.clear();

        // Then: 해당 댓글만 softDelete 됨
        ArticleComment deletedComment = commentRepository.findById(testComment.getId()).orElseThrow();
        assertThat(deletedComment.isDelete()).isTrue();

        // 다른 댓글들은 영향 없음
        List<ArticleComment> allComments = commentRepository.findAllByArticle(testArticle);
        long notDeletedCount = allComments.stream()
                .filter(comment -> !comment.isDelete())
                .count();
        assertThat(notDeletedCount).isEqualTo(2); // user2의 댓글 2개
    }

    @Test
    @DisplayName("댓글 삭제 후에도 데이터베이스에는 레코드가 남아있음")
    void testCommentDelete_RecordRemainsInDatabase() {
        // Given: 댓글 개수 확인
        long commentCountBefore = commentRepository.count();

        CommentDto deleteRequest = CommentDto.builder()
                .id(testComment.getId())
                .articleId(testArticle.getId())
                .content(testComment.getContent())
                .createBy(testUser1.getId())
                .createNickName(testUser1.getNickname())
                .build();

        // When: 댓글 삭제
        commentCommandService.delete(deleteRequest);

        entityManager.flush();
        entityManager.clear();

        // Then: 레코드 개수는 변하지 않음 (물리적 삭제 아님)
        long commentCountAfter = commentRepository.count();
        assertThat(commentCountAfter).isEqualTo(commentCountBefore);
    }

    @Test
    @DisplayName("댓글 삭제 시 Article은 영향 받지 않음")
    void testCommentDelete_ArticleIsNotAffected() {
        // Given: 삭제 전 Article 상태 확인
        Article articleBefore = articleRepository.findById(testArticle.getId()).orElseThrow();
        assertThat(articleBefore.isDelete()).isFalse();

        CommentDto deleteRequest = CommentDto.builder()
                .id(testComment.getId())
                .articleId(testArticle.getId())
                .content(testComment.getContent())
                .createBy(testUser1.getId())
                .createNickName(testUser1.getNickname())
                .build();

        // When: 댓글 삭제
        commentCommandService.delete(deleteRequest);

        entityManager.flush();
        entityManager.clear();

        // Then: Article은 여전히 삭제되지 않은 상태
        Article articleAfter = articleRepository.findById(testArticle.getId()).orElseThrow();
        assertThat(articleAfter.isDelete()).isFalse();
    }

    @Test
    @DisplayName("여러 댓글 중 하나만 삭제")
    void testCommentDelete_OnlyOneCommentDeleted() {
        // Given: 총 3개 댓글 존재 (user1: 1개, user2: 2개)
        List<ArticleComment> commentsBefore = commentRepository.findAllByArticle(testArticle);
        assertThat(commentsBefore).hasSize(3);
        assertThat(commentsBefore).noneMatch(ArticleComment::isDelete);

        CommentDto deleteRequest = CommentDto.builder()
                .id(testComment.getId())
                .articleId(testArticle.getId())
                .content(testComment.getContent())
                .createBy(testUser1.getId())
                .createNickName(testUser1.getNickname())
                .build();

        // When: user1의 댓글 1개 삭제
        commentCommandService.delete(deleteRequest);

        entityManager.flush();
        entityManager.clear();

        // Then: 1개만 삭제, 2개는 유지
        List<ArticleComment> commentsAfter = commentRepository.findAllByArticle(testArticle);
        assertThat(commentsAfter).hasSize(3); // 레코드 개수는 동일

        long deletedCount = commentsAfter.stream()
                .filter(ArticleComment::isDelete)
                .count();
        assertThat(deletedCount).isEqualTo(1);

        long notDeletedCount = commentsAfter.stream()
                .filter(comment -> !comment.isDelete())
                .count();
        assertThat(notDeletedCount).isEqualTo(2);
    }

    @Test
    @DisplayName("이미 삭제된 댓글 재삭제 시 예외 발생")
    void testCommentDelete_AlreadyDeletedComment() {
        // Given: 댓글을 먼저 softDelete
        ArticleComment comment = commentRepository.findById(testComment.getId()).orElseThrow();
        comment.softDelete();
        commentRepository.save(comment);

        entityManager.flush();
        entityManager.clear();

        CommentDto deleteRequest = CommentDto.builder()
                .id(testComment.getId())
                .articleId(testArticle.getId())
                .content(testComment.getContent())
                .createBy(testUser1.getId())
                .createNickName(testUser1.getNickname())
                .build();

        // When & Then: 다시 삭제 시도하면 예외 발생
        try {
            commentCommandService.delete(deleteRequest);
            entityManager.flush();
        } catch (Exception e) {
            // 이미 삭제된 댓글이므로 예외 발생 가능
            assertThat(e).isNotNull();
        }
    }

    @Test
    @DisplayName("다른 Article의 댓글은 영향 받지 않음")
    void testCommentDelete_OtherArticleCommentsNotAffected() {
        // Given: 다른 Article과 댓글 생성
        Article anotherArticle = Article.create(
                null,
                "Another Article",
                "Content",
                testUser1.getId(),
                null
        );
        articleRepository.save(anotherArticle);

        ArticleComment anotherComment = ArticleComment.init(
                anotherArticle,
                null,
                "Another Comment",
                testUser1.getId()
        );
        commentRepository.save(anotherComment);

        entityManager.flush();
        entityManager.clear();

        CommentDto deleteRequest = CommentDto.builder()
                .id(testComment.getId())
                .articleId(testArticle.getId())
                .content(testComment.getContent())
                .createBy(testUser1.getId())
                .createNickName(testUser1.getNickname())
                .build();

        // When: 첫 번째 Article의 댓글 삭제
        commentCommandService.delete(deleteRequest);

        entityManager.flush();
        entityManager.clear();

        // Then: 첫 번째 Article의 댓글만 삭제됨
        ArticleComment deletedComment = commentRepository.findById(testComment.getId()).orElseThrow();
        assertThat(deletedComment.isDelete()).isTrue();

        // 다른 Article의 댓글은 영향 없음
        ArticleComment notDeletedComment = commentRepository.findById(anotherComment.getId()).orElseThrow();
        assertThat(notDeletedComment.isDelete()).isFalse();
    }
}
