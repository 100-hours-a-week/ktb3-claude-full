package ktb.service;

import java.util.List;
import java.util.NoSuchElementException;

import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.domain.UserAccount;
import ktb.dto.ArticleDto;
import ktb.dto.PageInfoDto;
import ktb.dto.SaveArticleDto;
import ktb.dto.response.ArticleDetailDto;
import ktb.dto.response.ArticleSimpleDto;
import ktb.exception.AuthorizationException;
import ktb.exception.article.NoExistArticleException;
import ktb.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserService userService;

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
        return ArticleDetailDto.from(article);
    }

    public void save(SaveArticleDto updated) {
        UserAccount user = userService.getUserInfo(updated.userId()).toEntity();

        // Article 내용 변경
        if (updated.id() != null) {
            Article origin = articleRepository.findById(updated.id()).orElse(null);

            if (origin != null) {
                if (!origin.equalUserId(updated.userId())) {
                    throw new AuthorizationException(); // 403 Error
                }

                origin.update(updated.title(), updated.content());
                articleRepository.save(origin);

                return;
            }
        }

        // Article 신규 생성
        Article article = updated.toEntity(user);

        articleRepository.save(article);
    }

    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
