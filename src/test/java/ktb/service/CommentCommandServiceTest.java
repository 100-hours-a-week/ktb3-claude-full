package ktb.service;

import java.util.List;
import java.util.Optional;

import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.dto.CommentDto;
import ktb.exception.article.AlreadyDeletedArticle;
import ktb.exception.article.AlreadyDeletedComment;
import ktb.exception.article.NoExistArticleException;
import ktb.fixture.ArticleCommentFixture;
import ktb.fixture.ArticleFixture;
import ktb.handler.AbstractHandler;
import ktb.handler.context.CommentDeleteContext;
import ktb.handler.context.ContextData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentCommandServiceTest {
    @InjectMocks
    private CommentCommandService commentCommandService;

    @Mock
    private ArticleService articleService;

    @Mock
    private CommentService commentService;

    @Mock
    private AbstractHandler<ContextData<?>> commentDeleteHandlerChain;

    @Test
    @DisplayName("댓글 추가 시 게시글 존재하면 저장")
    void 댓글_추가() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        ArticleComment requestComment = ArticleCommentFixture.create(article, "hello", 10L);
        CommentDto request = CommentDto.from(requestComment);
        ArticleComment expectedComment = ArticleCommentFixture.create(article, "hello", 10L);

        when(articleService.findById(1L)).thenReturn(Optional.of(article));
        when(commentService.save(any(ArticleComment.class))).thenReturn(expectedComment);

        // when
        CommentDto response = commentCommandService.addComment(request);

        // then
        assertThat(response).isEqualTo(request);
        ArgumentCaptor<ArticleComment> captor = ArgumentCaptor.forClass(ArticleComment.class);
        verify(commentService).save(captor.capture());
        assertThat(captor.getValue().getContent()).isEqualTo(expectedComment.getContent());
    }

    @Test
    @DisplayName("댓글 추가 시 게시글 없으면 예외")
    void 댓글_추가_게시글없음() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        ArticleComment requestComment = ArticleCommentFixture.create(article, "hello", 10L);
        CommentDto request = CommentDto.from(requestComment);

        when(articleService.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentCommandService.addComment(request))
                .isInstanceOf(NoExistArticleException.class);
    }

    @Test
    @DisplayName("댓글 추가 시 게시글이 삭제 상태면 예외")
    void 댓글_추가_삭제된_게시글() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        article.softDelete();
        ArticleComment requestComment = ArticleCommentFixture.create(article, "hello", 10L);
        CommentDto request = CommentDto.from(requestComment);
        when(articleService.findById(1L)).thenReturn(Optional.of(article));

        // when & then
        assertThatThrownBy(() -> commentCommandService.addComment(request))
                .isInstanceOf(AlreadyDeletedArticle.class);
    }

    @Test
    @DisplayName("댓글 저장 시 삭제된 게시글이면 예외")
    void 댓글_수정_삭제된_게시글() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        ArticleComment requestComment = ArticleCommentFixture.create(article, 2L, "update", 10L);
        CommentDto dto = CommentDto.from(requestComment);

        when(articleService.existsByIdAndIsDeleted(1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> commentCommandService.save(dto))
                .isInstanceOf(AlreadyDeletedArticle.class);
    }

    @Test
    @DisplayName("댓글 저장 시 댓글이 없으면 예외")
    void 댓글_수정_댓글없음() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        ArticleComment requestComment = ArticleCommentFixture.create(article, 2L, "update", 10L);
        CommentDto dto = CommentDto.from(requestComment);

        when(articleService.existsByIdAndIsDeleted(1L)).thenReturn(false);
        when(commentService.findById(2L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentCommandService.save(dto))
                .isInstanceOf(NoExistArticleException.class);
    }

    @Test
    @DisplayName("댓글 저장 시 삭제된 댓글이면 예외")
    void 댓글_수정_삭제된_댓글() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        ArticleComment comment = ArticleCommentFixture.create(article, 2L, "old", 10L);
        comment.softDelete();
        ArticleComment requestComment = ArticleCommentFixture.create(article, 2L, "update", 10L);
        CommentDto dto = CommentDto.from(requestComment);

        when(articleService.existsByIdAndIsDeleted(1L)).thenReturn(false);
        when(commentService.findById(2L)).thenReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentCommandService.save(dto))
                .isInstanceOf(AlreadyDeletedComment.class);
    }

    @Test
    @DisplayName("댓글 저장 성공 시 내용 업데이트")
    void 댓글_수정_성공() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        ArticleComment comment = ArticleCommentFixture.create(article, 2L, "old", 10L);
        ArticleComment requestComment = ArticleCommentFixture.create(article, 2L, "update", 10L);
        CommentDto dto = CommentDto.from(requestComment);

        when(articleService.existsByIdAndIsDeleted(1L)).thenReturn(false);
        when(commentService.findById(2L)).thenReturn(Optional.of(comment));

        // when
        CommentDto result = commentCommandService.save(dto);

        // then
        assertThat(comment.getContent()).isEqualTo("update");
        assertThat(result.content()).isEqualTo("update");
    }

    @Test
    @DisplayName("엔티티 기반 저장은 CommentService에 위임")
    void 댓글_엔티티_저장() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        ArticleComment comment = ArticleCommentFixture.create(article, 2L, "old", 10L);
        when(commentService.save(comment)).thenReturn(comment);

        // when
        CommentDto result = commentCommandService.save(comment);

        // then
        assertThat(result.id()).isEqualTo(comment.getId());
        verify(commentService).save(comment);
    }

    @Test
    @DisplayName("댓글 삭제 시 핸들러 체인은 반드시 호출")
    void 댓글_삭제() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        ArticleComment requestComment = ArticleCommentFixture.create(article, 1L, "content", 10L);
        CommentDto dto = CommentDto.from(requestComment);

        // when
        commentCommandService.delete(dto);

        // then
        verify(commentDeleteHandlerChain).handle(any(CommentDeleteContext.class));
    }

    @Test
    @DisplayName("사용자 댓글 조회는 DTO로 변환")
    void 사용자_댓글_조회() {
        // given
        Article article = ArticleFixture.createWithId(1L);
        List<ArticleComment> comments = List.of(
                ArticleCommentFixture.create(article, 1L, "first", 10L),
                ArticleCommentFixture.create(article, 2L, "second", 10L)
        );
        when(commentService.findAllByUserId(10L)).thenReturn(comments);

        // when
        List<CommentDto> result = commentCommandService.findAllByUserId(10L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CommentDto::content).containsExactly("first", "second");
        verify(commentService).findAllByUserId(10L);
    }
}
