package ktb.service;

import ktb.domain.Article;
import ktb.domain.ArticleMeta;
import ktb.dto.SaveArticleDto;
import ktb.exception.article.NoExistArticleException;
import ktb.fixture.ArticleFixture;
import ktb.handler.AbstractHandler;
import ktb.handler.context.ContextData;
import ktb.handler.context.SoftDeleteContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleCommandService 단위 테스트")
class ArticleCommandServiceTest {

    @InjectMocks
    private ArticleCommandService articleCommandService;

    @Mock
    private ArticleService articleService;

    @Mock
    private ArticleMetaService metaService;

    @Mock
    private AbstractHandler<ContextData<?>> articleDeleteHandlerChain;

    @Test
    @DisplayName("새 Article 저장 - Article과 Meta 모두 저장")
    void 게시글_등록_게시글_메타_저장() {
        // Given
        SaveArticleDto saveDto = SaveArticleDto.builder()
                .title("New Article")
                .content("New Content")
                .userId(1L)
                .articleImagePath(null)
                .build();

        // When
        articleCommandService.save(saveDto);

        // Then
        verify(articleService).save(any(Article.class));
        verify(metaService).save(any(ArticleMeta.class));
    }

    @Test
    @DisplayName("기존 Article 수정 - 존재하는 Article 업데이트")
    void 게시글_수정_제목_내용_반영_여부() {
        // Given
        Article testArticle = ArticleFixture.createWithId(1L);

        SaveArticleDto saveDto = SaveArticleDto.builder()
                .id(1L)
                .title("Updated Title")
                .content("Updated Content")
                .userId(1L)
                .build();

        when(articleService.findById(1L)).thenReturn(Optional.of(testArticle));

        // When
        articleCommandService.save(saveDto);

        // Then
        verify(articleService).findById(1L);
        verify(articleService).save(testArticle);
        verify(metaService, never()).save(any());

        // Article이 업데이트 되었는지 확인
        assertThat(testArticle.getTitle()).isEqualTo("Updated Title");
        assertThat(testArticle.getContent()).isEqualTo("Updated Content");
    }

    @Test
    @DisplayName("존재하지 않는 Article 수정 시도 - 예외 발생")
    void 미존재_게시글_수정_예외_발생() {
        // Given
        SaveArticleDto saveDto = SaveArticleDto.builder()
                .id(999L)
                .title("Updated Title")
                .content("Updated Content")
                .userId(1L)
                .build();

        when(articleService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> articleCommandService.save(saveDto))
                .isInstanceOf(NoExistArticleException.class);

        verify(articleService, never()).save(any());
    }

    @Test
    @DisplayName("Article 삭제 - Handler 체인 호출 확인")
    void 게시글_삭제_핸들러_호출_여부() {
        // Given
        Long userId = 1L;
        Long articleId = 1L;

        when(articleDeleteHandlerChain.handle(any(SoftDeleteContext.class)))
                .thenReturn(true);

        // When
        articleCommandService.delete(userId, articleId);

        // Then
        verify(articleDeleteHandlerChain).handle(any(SoftDeleteContext.class));
    }

    @Test
    @DisplayName("Article 수정 시 title만 변경")
    void 게시글_수정_제목만_반영() {
        // Given
        Article testArticle = ArticleFixture.createWithId(1L);

        SaveArticleDto saveDto = SaveArticleDto.builder()
                .id(1L)
                .title("New Title Only")
                .content(null)  // content는 변경하지 않음
                .userId(1L)
                .build();

        when(articleService.findById(1L)).thenReturn(Optional.of(testArticle));

        // When
        articleCommandService.save(saveDto);

        // Then
        verify(articleService).save(testArticle);
        assertThat(testArticle.getTitle()).isEqualTo("New Title Only");
        assertThat(testArticle.getContent()).isEqualTo(testArticle.getContent());  // 기존 값 유지
    }

    @Test
    @DisplayName("Article 수정 시 content만 변경")
    void 게시글_수정_내용만_반영() {
        // Given
        Article testArticle = ArticleFixture.createWithId(1L);

        SaveArticleDto saveDto = SaveArticleDto.builder()
                .id(1L)
                .title(null)  // title은 변경하지 않음
                .content("New Content Only")
                .userId(1L)
                .build();

        when(articleService.findById(1L)).thenReturn(Optional.of(testArticle));

        // When
        articleCommandService.save(saveDto);

        // Then
        verify(articleService).save(testArticle);
        assertThat(testArticle.getTitle()).isEqualTo(testArticle.getTitle());  // 기존 값 유지
        assertThat(testArticle.getContent()).isEqualTo("New Content Only");
    }

    @Test
    @DisplayName("새 Article 저장 시 imagePath 포함")
    void 게시글_저장_이미지_포함_여부() {
        // Given
        SaveArticleDto saveDto = SaveArticleDto.builder()
                .title("Article with Image")
                .content("Content")
                .userId(1L)
                .articleImagePath("/images/test.jpg")
                .build();

        // When
        articleCommandService.save(saveDto);

        // Then
        verify(articleService).save(any(Article.class));
        verify(metaService).save(any(ArticleMeta.class));
    }
}
