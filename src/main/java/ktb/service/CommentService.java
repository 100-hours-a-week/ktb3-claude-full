package ktb.service;

import java.util.List;
import java.util.Optional;

import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.repository.ArticleCommentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleCommentRepository commentRepository;

    public Optional<ArticleComment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public List<ArticleComment> findAllByArticle(Article article) {
        return commentRepository.findAllByArticle(article);
    }

    public ArticleComment save(ArticleComment comment) {
        return commentRepository.save(comment);
    }

    public List<ArticleComment> findAllByUserId(Long userId) {
        return commentRepository.findAllByCreateBy_Id(userId);
    }

    public List<ArticleComment> findAllByUserIdAndArticleId(Long userId, Long articleId) {
        return commentRepository.findAllByCreateBy_IdAndArticle_Id(userId, articleId);
    }
}
