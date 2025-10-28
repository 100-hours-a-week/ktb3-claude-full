package ktb.service;

import java.util.List;
import java.util.Optional;

import ktb.domain.Article;
import ktb.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public Optional<Article> findById(Long articleId) {
        return articleRepository.findById(articleId);
    }

    public List<Article> findAllByOrderByIdAsc(Limit limit) {
        return articleRepository.findAllByOrderByIdAsc(limit);
    }

    public List<Article> findAllByIdGreaterThan(Long cursorId, Limit limit) {
        return articleRepository.findAllByIdGreaterThanOrderByIdAsc(cursorId, limit);
    }

    public Article save(Article article) {
        return articleRepository.save(article);
    }
}
