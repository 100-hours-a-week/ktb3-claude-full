package ktb.service;

import java.util.List;
import java.util.Optional;

import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.dto.CommentDto;
import ktb.exception.article.NoExistArticleException;
import ktb.fixture.ArticleCommentFixture;
import ktb.fixture.ArticleFixture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentQueryServiceTest {
    @InjectMocks
    private CommentQueryService commentQueryService;

    @Mock
    private ArticleService articleService;

    @Mock
    private CommentService commentService;

    @Test
    @DisplayName("게시글 ID로 댓글 목록 조회 성공")
    void 댓글_목록_조회() {
        // given
        Long articleId = 1L;
        Article article = ArticleFixture.createWithId(articleId);
        List<ArticleComment> comments = List.of(
                ArticleCommentFixture.create(article, 1L, "first", 10L),
                ArticleCommentFixture.create(article, 2L, "second", 11L)
        );

        when(articleService.findById(articleId)).thenReturn(Optional.of(article));
        when(commentService.findAllByArticle(article)).thenReturn(comments);

        // when
        List<CommentDto> result = commentQueryService.searchCommentsByArticleId(articleId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CommentDto::content).containsExactly("first", "second");
        verify(articleService).findById(articleId);
        verify(commentService).findAllByArticle(article);
    }

    @Test
    @DisplayName("게시글이 없으면 댓글 조회 시 예외 발생")
    void 댓글_목록_조회_게시글없음() {
        // given
        Long articleId = 99L;
        when(articleService.findById(articleId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentQueryService.searchCommentsByArticleId(articleId))
                .isInstanceOf(NoExistArticleException.class);
        verify(articleService).findById(articleId);
        verify(commentService, never()).findAllByArticle(any(Article.class));
    }
}
