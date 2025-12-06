package ktb.service;

import java.util.Optional;

import ktb.domain.ArticleMeta;
import ktb.exception.article.NoExistArticleException;
import ktb.fixture.ArticleFixture;
import ktb.repository.ArticleMetaRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleMetaServiceTest {
    @InjectMocks
    private ArticleMetaService articleMetaService;

    @Mock
    private ArticleMetaRepository metaRepository;

    @Test
    @DisplayName("양수 delta면 좋아요 수 증가")
    void 좋아요_증가() {
        // given
        Long articleId = 1L;
        ArticleMeta meta = spy(ArticleFixture.createMeta(articleId));
        when(metaRepository.findById(articleId)).thenReturn(Optional.of(meta));

        // when
        articleMetaService.applyLikeDelta(articleId, 1);

        // then
        int expectedLikeCount = 1;
        assertThat(meta.getLikeCnt().get()).isEqualTo(expectedLikeCount);
        verify(metaRepository).findById(articleId);
        verify(meta, times(1)).increaseLikeCnt();
    }

    @Test
    @DisplayName("음수 delta면 좋아요 수 감소, 0보다 작아지지 않음")
    void 좋아요_감소() {
        // given
        Long articleId = 1L;
        ArticleMeta meta = spy(ArticleFixture.createMeta(articleId));
        when(metaRepository.findById(articleId)).thenReturn(Optional.of(meta));
        articleMetaService.applyLikeDelta(articleId, 1);
        articleMetaService.applyLikeDelta(articleId, 1);

        // when
        articleMetaService.applyLikeDelta(articleId, -1);
        articleMetaService.applyLikeDelta(articleId, -1);
        articleMetaService.applyLikeDelta(articleId, -1);

        // then
        int expectedLikeCount = 0;
        assertThat(meta.getLikeCnt().get()).isEqualTo(expectedLikeCount);
        verify(meta, times(3)).decreaseLikeCnt();
    }

    @Test
    @DisplayName("조회수 증가 호출 시 ViewCnt 증가")
    void 조회수_증가() {
        // given
        Long articleId = 2L;
        ArticleMeta meta = spy(ArticleFixture.createMeta(articleId));
        when(metaRepository.findById(articleId)).thenReturn(Optional.of(meta));

        // when
        articleMetaService.increaseViewCount(articleId);

        // then
        int expectedViewCount = 1;
        assertThat(meta.getViewCnt().get()).isEqualTo(expectedViewCount);
        verify(meta, times(1)).increaseViewCnt();
    }

    @Test
    @DisplayName("메타 정보가 없으면 예외 발생")
    void 메타없음_예외() {
        // given
        Long articleId = 99L;
        when(metaRepository.findById(articleId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleMetaService.applyLikeDelta(articleId, 1))
                .isInstanceOf(NoExistArticleException.class);
    }

}
