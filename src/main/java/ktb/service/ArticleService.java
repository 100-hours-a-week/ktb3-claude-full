package ktb.service;

import java.util.List;
import java.util.Optional;

import ktb.domain.Article;
import ktb.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public Optional<Article> findById(Long articleId) {
        return articleRepository.findById(articleId);
    }

    public Optional<Article> findForDelete(Long articleId) {
        return articleRepository.findForDelete(articleId);
    }

    public Optional<Article> findDetail(Long articleId) {
        return articleRepository.findDetail(articleId);
    }

    @Transactional(readOnly = true)
    public List<Article> findAllByOrderByIdAsc(Limit limit) {
        return articleRepository.findAllByOrderByIdAsc(limit);
    }

    @Transactional(readOnly = true)
    public List<Article> findAllByIdGreaterThan(Long cursorId, Limit limit) {
        return articleRepository.findAllByIdGreaterThanOrderByIdAsc(cursorId, limit);
    }

    @Transactional
    public Article save(Article article) {
        return articleRepository.save(article);
    }

    @Transactional
    public List<Article> saveAll(List<Article> articles) {
        return articleRepository.saveAll(articles);
    }

    @Transactional
    public List<Article> findByCreateBy_Id(Long userId) {
        return articleRepository.findByCreateBy_Id(userId);
    }

    public boolean existsByIdAndIsDeleted(Long articleId) {
        return articleRepository.existsByIdAndIsDeletedTrue(articleId);
    }
}
