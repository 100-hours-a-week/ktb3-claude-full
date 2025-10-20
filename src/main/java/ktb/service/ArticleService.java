package ktb.service;

import java.util.List;

import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.dto.ArticleDto;
import ktb.dto.PageInfoDto;
import ktb.dto.SaveArticleDto;
import ktb.dto.response.ArticleDetailDto;
import ktb.dto.response.ArticleSimpleDto;
import ktb.dto.response.CommentDetailDto;
import ktb.exception.article.NoExistArticleException;
import ktb.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public Slice<ArticleSimpleDto> findAll(PageInfoDto pageInfo) {
        Slice<Article> articleSlice = articleRepository.findAll(pageInfo.endCursor(), pageInfo.size());

        List<ArticleSimpleDto> simpleDtoList = articleSlice.content().stream()
                .map(ArticleSimpleDto::from)
                .toList();

        return Slice.of(simpleDtoList, articleSlice.hasNext(), articleSlice.nextCursor());
    }

    public ArticleDto findById(Long articleId) {
        return ArticleDto.from(articleRepository.findById(articleId).orElseThrow(NoExistArticleException::new));
    }

    public ArticleDetailDto findByIdDetail(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(NoExistArticleException::new);

        // CommentDetailDto 리스트 생성 (저장된 nickname 사용)
        List<CommentDetailDto> commentDetailDtoList = article.getAllComments().stream()
                .map(CommentDetailDto::from)
                .toList();

        return ArticleDetailDto.from(article, commentDetailDtoList);
    }

    public void save(SaveArticleDto updated) {
        // Article 내용 변경
        if (updated.id() != null) {
            Article origin = articleRepository.findById(updated.id()).orElse(null);

            if (origin != null) {
                // 인가는 @Authorized AOP 에서 확인
                origin.update(updated.title(), updated.content());
                articleRepository.save(origin);

                return;
            }
        }

        // Article 신규 생성
        Article article = updated.toEntity();

        articleRepository.save(article);
    }

    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
