package ktb.event.article;

import ktb.event.context.ArticleLikeEvent;
import ktb.event.context.ArticleViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void publishLikeDelta(Long articleId, int delta) {
        eventPublisher.publishEvent(new ArticleLikeEvent(articleId, delta));
    }
}
