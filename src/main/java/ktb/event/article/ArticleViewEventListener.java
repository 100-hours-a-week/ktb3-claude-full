package ktb.event.article;

import ktb.event.context.ArticleViewEvent;
import ktb.service.ArticleMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleViewEventListener {
    private final ArticleMetaService articleMetaService;

    @Async
    @EventListener
    public void handleViewEvent(ArticleViewEvent event) {
        articleMetaService.increaseViewCount(event.articleId());
    }
}
