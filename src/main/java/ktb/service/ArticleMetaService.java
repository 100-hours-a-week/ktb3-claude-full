package ktb.service;

import ktb.domain.ArticleMeta;
import ktb.repository.ArticleMetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleMetaService {
    private final ArticleMetaRepository metaRepository;

    public ArticleMeta save(ArticleMeta meta) {
        return metaRepository.save(meta);
    }
}
