package ktb.service;

import java.util.Optional;

import ktb.domain.ArticleLike;
import ktb.domain.LikeTargetType;
import ktb.event.article.ArticleEventPublisher;
import ktb.repository.ArticleLikeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleEventPublisher articleEventPublisher;

    @Transactional
    public boolean toggleLike(Long articleId, Long userId) {
        Optional<ArticleLike> existing = articleLikeRepository.findByTargetIdAndUserIdAndType(
                articleId,
                userId,
                LikeTargetType.ARTICLE
        );

        ArticleLike like = existing.orElseGet(() -> createArticleLike(articleId, userId));

        if (like.isLike()) {
            like.cancel();
            articleEventPublisher.publishLikeDelta(articleId, -1);
            return false;
        }

        like.like();
        articleEventPublisher.publishLikeDelta(articleId, 1);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long articleId, Long userId) {
        return articleLikeRepository.existsByTargetIdAndUserIdAndTypeAndIsLikeTrue(
                articleId,
                userId,
                LikeTargetType.ARTICLE
        );
    }

    private ArticleLike createArticleLike(Long articleId, Long userId) {
        ArticleLike like = ArticleLike.ofArticle(articleId, userId);

        return articleLikeRepository.save(like);
    }
}
