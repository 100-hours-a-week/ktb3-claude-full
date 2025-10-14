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
        return ArticleDto.from(articleRepository.findById(articleId).orElseThrow(NoSuchElementException::new));
    }

    public ArticleDetailDto findByIdDetail(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(NoSuchElementException::new);
        return ArticleDetailDto.from(article);
    }

    public void save(SaveArticleDto updated) {
        UserAccount user = userService.getUserInfo(updated.userId()).toEntity();

        Article origin = articleRepository.findById(updated.id()).orElse(null);
        // Article 변경
        if (origin != null) {
            if (!origin.equalUserId(updated.userId())) {
                throw new AuthorizationException(); // 403Error
            }

            origin.update(updated.title(), updated.content());
            articleRepository.save(origin);

            return;
        }

        // Article 신규 생성
        Article article =
                ArticleDto.of(updated.title(), updated.content(), user, updated.articleImagePath())
                        .toEntity();

        articleRepository.save(article);
    }

    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
