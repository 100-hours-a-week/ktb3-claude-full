package ktb.service;

import java.util.NoSuchElementException;

import ktb.common.pagination.Slice;
import ktb.domain.Article;
import ktb.domain.UserAccount;
import ktb.dto.ArticleDto;
import ktb.dto.PageInfoDto;
import ktb.dto.SaveArticleDto;
import ktb.exception.AuthorizationException;
import ktb.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserService userService;

    public Slice<ArticleDto> findAll(PageInfoDto pageInfo) {
        Long cursorId = articleRepository.getNextCursor(pageInfo.endCursor()).orElseThrow(NoSuchElementException::new);
        return ArticleDto.from(articleRepository.findAll(cursorId, pageInfo.size()));
    }

    public ArticleDto findById(Long articleId) {
        return ArticleDto.from(articleRepository.findById(articleId).orElseThrow(NoSuchElementException::new));
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
