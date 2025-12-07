package ktb.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.dto.PageInfoDto;
import ktb.dto.response.ArticleSimpleDto;
import ktb.event.article.ArticleEventPublisher;
import ktb.fixture.ArticleFixture;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.exception.article.NoExistArticleException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleQueryService 단위 테스트")
public class ArticleQueryServiceTest {
    @InjectMocks
    private ArticleQueryService articleQueryService;

    @Mock
    private ArticleService articleService;

    @Mock
    private ArticleLikeService articleLikeService;

    @Mock
    private ArticleEventPublisher articleEventPublisher;

    @Test
    @DisplayName("커서 없이 게시글 목록 조회 시 size 만큼 반환")
    void 게시글_목록_조회_커서_없음() {
        // Given
        PageInfoDto pageInfo = new PageInfoDto(null, 20);
        List<Article> articles = ArticleFixture.createSequentialList(1L, 21);
        when(articleService.findAllByOrderByIdAsc(any(Limit.class))).thenReturn(articles);

        // When
        Slice<ArticleSimpleDto> result = articleQueryService.findByIdAndCursorPagination(pageInfo);

        // Then
        verify(articleService).findAllByOrderByIdAsc(any(Limit.class));
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo(20L);
        assertThat(result.content())
                .hasSize(pageInfo.size())
                .extracting(ArticleSimpleDto::id)
                .containsExactlyElementsOf(
                        LongStream.rangeClosed(1, pageInfo.size()).boxed().toList()
                );
    }

    @Test
    @DisplayName("커서 기반으로 다음 페이지 조회 시 커서보다 큰 게시글만 반환")
    void 게시글_목록_조회_커서_기준() {
        // Given
        PageInfoDto pageInfo = new PageInfoDto(10L, 5);
        List<Article> articles = ArticleFixture.createSequentialList(11L, 6);
        when(articleService.findAllByIdGreaterThan(eq(10L), any(Limit.class))).thenReturn(articles);

        // When
        Slice<ArticleSimpleDto> result = articleQueryService.findByIdAndCursorPagination(pageInfo);

        // Then
        verify(articleService).findAllByIdGreaterThan(eq(10L), any(Limit.class));
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo(15L);
        assertThat(result.content())
                .extracting(ArticleSimpleDto::id)
                .containsExactly(11L, 12L, 13L, 14L, 15L);
    }

    @Test
    @DisplayName("게시글 상세 조회 시 존재하지 않으면 예외 발생")
    void 게시글_상세_조회_미존재_예외() {
        // Given
        Long articleId = 1L;
        when(articleService.findDetail(articleId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> articleQueryService.findByIdDetail(articleId, 1L))
                .isInstanceOf(NoExistArticleException.class);
    }

    @Test
    @DisplayName("삭제된 게시글 상세 조회 시 예외 발생")
    void 게시글_상세_조회_삭제된_게시글_예외() {
        // Given
        Long articleId = 1L;
        Article deletedArticle = ArticleFixture.createWithId(articleId);
        deletedArticle.softDelete();
        when(articleService.findDetail(articleId)).thenReturn(Optional.of(deletedArticle));

        // When & Then
        assertThatThrownBy(() -> articleQueryService.findByIdDetail(articleId, 1L))
                .isInstanceOf(AlreadyDeletedArticle.class);
    }

    @Test
    @DisplayName("상세 조회 시 조회수 이벤트 발행")
    void 게시글_상세_조회_조회수_반영() {
        // Given
        Long articleId = 1L;
        Article article = ArticleFixture.createWithId(articleId);
        when(articleService.findDetail(articleId)).thenReturn(Optional.of(article));
        when(articleLikeService.isLiked(articleId, 1L)).thenReturn(false);

        // When
        articleQueryService.findByIdDetail(articleId, 1L);

        // Then
        verify(articleEventPublisher).publishView(articleId);
    }
}
