package ktb.event.article;

import ktb.event.context.ArticleLikeEvent;
import ktb.service.ArticleMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleLikeEventListener {
    private final ArticleMetaService articleMetaService;

    @Async
    @EventListener
    public void handleLikeEvent(ArticleLikeEvent event) {
        articleMetaService.applyLikeDelta(event.articleId(), event.delta());
    }
}
