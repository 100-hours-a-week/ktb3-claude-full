package ktb.service;

import java.util.Optional;

import ktb.domain.ArticleLike;
import ktb.event.article.ArticleEventPublisher;
import ktb.repository.ArticleLikeRepository;
import ktb.domain.LikeTargetType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleLikeServiceTest {
    @InjectMocks
    private ArticleLikeService articleLikeService;

    @Mock
    private ArticleLikeRepository articleLikeRepository;

    @Mock
    private ArticleEventPublisher articleEventPublisher;

    @Test
    @DisplayName("좋아요가 이미 눌린 상태면 취소하고 false 반환")
    void 좋아요_토글_이미_좋아요면_취소() {
        // given
        Long articleId = 1L;
        Long userId = 10L;
        ArticleLike liked = ArticleLike.ofArticle(articleId, userId);
        when(articleLikeRepository.findByTargetIdAndUserIdAndType(eq(articleId), eq(userId), any(LikeTargetType.class)))
                .thenReturn(Optional.of(liked));

        // when
        boolean result = articleLikeService.toggleLike(articleId, userId);

        // then
        assertThat(result).isFalse();
        assertThat(liked.isLike()).isFalse();
        verify(articleEventPublisher).publishLikeDelta(articleId, -1);
        verify(articleLikeRepository, never()).save(any());
    }

    @Test
    @DisplayName("좋아요가 취소된 상태면 다시 좋아요 처리하고 true 반환")
    void 좋아요_토글_취소상태면_좋아요() {
        // given
        Long articleId = 1L;
        Long userId = 10L;
        ArticleLike canceled = ArticleLike.ofArticle(articleId, userId);
        canceled.cancel();
        when(articleLikeRepository.findByTargetIdAndUserIdAndType(eq(articleId), eq(userId), any(LikeTargetType.class)))
                .thenReturn(Optional.of(canceled));

        // when
        boolean result = articleLikeService.toggleLike(articleId, userId);

        // then
        assertThat(result).isTrue();
        assertThat(canceled.isLike()).isTrue();
        verify(articleEventPublisher).publishLikeDelta(articleId, 1);
        verify(articleLikeRepository, never()).save(any());
    }

    @Test
    @DisplayName("최초 좋아요 시 엔티티를 생성하고 true 반환")
    void 좋아요_토글_최초면_생성하여_좋아요() {
        // given
        Long articleId = 1L;
        Long userId = 10L;
        ArticleLike saved = ArticleLike.ofArticle(articleId, userId);
        saved.cancel(); // 저장 직후에는 아직 좋아요 누르지 않은 상태로 가정

        when(articleLikeRepository.findByTargetIdAndUserIdAndType(eq(articleId), eq(userId), any(LikeTargetType.class)))
                .thenReturn(Optional.empty());
        when(articleLikeRepository.save(any(ArticleLike.class))).thenReturn(saved);

        // when
        boolean result = articleLikeService.toggleLike(articleId, userId);

        // then
        assertThat(result).isTrue();
        assertThat(saved.isLike()).isTrue();
        verify(articleLikeRepository).save(any(ArticleLike.class));
        verify(articleEventPublisher).publishLikeDelta(articleId, 1);
    }

    @Test
    @DisplayName("좋아요 여부 조회 시 Repository 결과를 그대로 반환")
    void 좋아요_여부_조회() {
        // given
        Long articleId = 1L;
        Long userId = 10L;
        when(articleLikeRepository.existsByTargetIdAndUserIdAndTypeAndIsLikeTrue(eq(articleId), eq(userId), any(LikeTargetType.class)))
                .thenReturn(true);

        // when
        boolean liked = articleLikeService.isLiked(articleId, userId);

        // then
        assertThat(liked).isTrue();
        verify(articleLikeRepository)
                .existsByTargetIdAndUserIdAndTypeAndIsLikeTrue(eq(articleId), eq(userId), any(LikeTargetType.class));
    }
}
