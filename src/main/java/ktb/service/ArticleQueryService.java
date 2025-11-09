package ktb.service;

import java.util.Comparator;
import java.util.List;

import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.dto.PageInfoDto;
import ktb.dto.response.ArticleDetailDto;
import ktb.dto.response.ArticleSimpleDto;
import ktb.dto.response.CommentDetailDto;
import ktb.exception.article.NoExistArticleException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Article 조회 전용 서비스 (CQS 패턴 - Query)
 * 읽기 작업만 수행하며, 데이터 변경 작업은 수행하지 않음
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleQueryService {
    private final ArticleService articleService;

    /**
     * 커서 기반 페이징으로 Article 목록 조회
     *
     * @param pageInfo 페이징 정보 (커서, 사이즈)
     * @return 페이징된 Article 목록
     */
    public Slice<ArticleSimpleDto> findByIdAndCursorPagination(PageInfoDto pageInfo) {
        List<Article> articles;
        Limit limit = Limit.of(pageInfo.size() + 1);

        if (pageInfo.endCursor() == null) {
            articles = articleService.findAllByOrderByIdAsc(limit);
        } else {
            articles = articleService.findAllByIdGreaterThan(pageInfo.endCursor(), limit);
        }

        int size = pageInfo.size();
        boolean hasNext = articles.size() > size;
        List<Article> content = hasNext ? articles.subList(0, size) : articles;

        Long nextCursor = hasNext
                ? content.get(content.size() - 1).getId()
                : null;

        List<ArticleSimpleDto> simpleDtoList = content.stream()
                .map(ArticleSimpleDto::from)
                .toList();

        return Slice.of(simpleDtoList, hasNext, nextCursor);
    }

    /**
     * Article 상세 조회 (댓글 포함)
     *
     * @param articleId Article ID
     * @param userId 현재 사용자 ID (권한 확인용, null 가능)
     * @return Article 상세 정보 + 댓글 목록
     * @throws NoExistArticleException Article 존재하지 않을 경우
     */
    public ArticleDetailDto findByIdDetail(Long articleId, Long userId) {
        Article article = articleService.findDetail(articleId)
                .orElseThrow(NoExistArticleException::new);

        article.getComments().sort(Comparator.comparing(ArticleComment::getId));

        return ArticleDetailDto.from(article, userId);
    }
}
