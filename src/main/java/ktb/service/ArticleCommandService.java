package ktb.service;

import ktb.domain.Article;
import ktb.dto.SaveArticleDto;
import ktb.exception.article.NoExistArticleException;
import ktb.handler.AbstractHandler;
import ktb.handler.context.ContextData;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.context.payload.SoftDeletePayload;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Article 수정 전용 서비스 (CQS 패턴 - Command)
 * 데이터 변경 작업(생성, 수정, 삭제)만 수행하며, 조회 작업은 수행하지 않음
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleCommandService {
    private final ArticleService articleService;

    private final ArticleMetaService metaService;

    private final AbstractHandler<ContextData<?>> articleDeleteHandlerChain;

    /**
     * Article 저장 (생성 또는 수정)
     *
     * @param saveDto Article 저장 정보
     */
//    @PostAuthorize("hasRole('ADMIN') or returnObject == null or returnObject.createBy == null or returnObject.createBy.id == authentication.principal.account.id")
    public Article save(SaveArticleDto saveDto) {
        // Article 내용 변경
        if (saveDto.id() != null) {
            Article origin = articleService.findById(saveDto.id())
                    .orElseThrow(NoExistArticleException::new);

            origin.update(saveDto.title(), saveDto.content());
            return articleService.save(origin);
        }

        // Article 신규 생성
        Article article = saveDto.toEntity();
        metaService.save(article.getMeta());
        return articleService.save(article);
    }


    /**
     * Article 삭제 (Soft Delete)
     *
     * @param id Article ID
     */
//    @PreAuthorize("hasRole('ADMIN') or @articleService.findById(#id).get().getCreateBy().id().equals(#userId)")
    public void delete(Long userId, Long id) {
        // Article 단일 삭제: traceId는 null (권한은 AOP 로 확인), payload: articleId 설정
        SoftDeleteContext context = new SoftDeleteContext(
                null,
                new SoftDeletePayload(null, id)
        );

        // Handler 체인을 통한 소프트 삭제 처리 (Execution → Audit)
        // Execution 단계에서 SingleArticleDeleteStrategy, ArticleCommentsDeleteStrategy 가 순서대로 실행
        articleDeleteHandlerChain.handle(context);
    }
}
