package ktb.service;

import ktb.domain.ArticleMeta;
import ktb.exception.article.NoExistArticleException;
import ktb.repository.ArticleMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleMetaService {
    private final ArticleMetaRepository metaRepository;

    public ArticleMeta save(ArticleMeta meta) {
        return metaRepository.save(meta);
    }

    @Transactional
    public void applyLikeDelta(Long articleId, int delta) {
        ArticleMeta meta = requireMeta(articleId);

        if (delta > 0) {
            meta.increaseLikeCnt();
        } else {
            meta.decreaseLikeCnt();
        }
    }

    @Transactional
    public void increaseViewCount(Long articleId) {
        ArticleMeta meta = requireMeta(articleId);
        meta.increaseViewCnt();
    }

    private ArticleMeta requireMeta(Long articleId) {
        return metaRepository.findById(articleId)
                .orElseThrow(NoExistArticleException::new);
    }
}
