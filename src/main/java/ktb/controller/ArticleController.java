package ktb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import jakarta.validation.constraints.Min;
import ktb.auth.adapter.SecurityUserAccount;
import ktb.common.pagination.Slice;
import ktb.constant.MessageConstant.Success;
import ktb.dto.PageInfoDto;
import ktb.dto.SaveArticleDto;
import ktb.dto.request.AllArticleRetrieveRequest;
import ktb.dto.request.ArticlePatchRequest;
import ktb.dto.request.ArticleRequest;
import ktb.dto.response.ArticleDetailDto;
import ktb.dto.response.ArticleLikeResponse;
import ktb.dto.response.ArticleSimpleDto;
import ktb.dto.response.ArticlesResponse;
import ktb.dto.response.CommonResponse;
import ktb.service.ArticleCommandService;
import ktb.service.ArticleLikeService;
import ktb.service.ArticleQueryService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Article", description = "게시글 리소스 관련 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleQueryService articleQueryService;
    private final ArticleCommandService articleCommandService;
    private final ArticleLikeService articleLikeService;

    @Operation(
            summary = "Article search(page)",
            description = "게시글들의 페이지 조회합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = AllArticleRetrieveRequest.class))),
            }
    )
    @GetMapping("/articles")
    public ResponseEntity<ArticlesResponse<ArticleSimpleDto>> getAll(
            @Parameter(description = "조회할 게시글의 ID(해당 ID 초과 조회)", example = "0")
            @RequestParam Long after,

            @Parameter(description = "조회할 게시글 개수", example = "10")
            @Min(1)
            @RequestParam int limit
    ) {
        PageInfoDto pageInfo = PageInfoDto.of(after, limit);

        Slice<ArticleSimpleDto> page = articleQueryService.findByIdAndCursorPagination(pageInfo);

        return ResponseEntity.ok(ArticlesResponse.of(Success.RETRIEVAL_ALL, page.getData(), page.getPageInfo()));
    }

    @Operation(
            summary = "Article insert",
            description = "게시글 단건 등록합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", content = @Content(schema = @Schema(implementation = ArticleRequest.class))),
            }
    )
    @PostMapping("/article")
    public ResponseEntity<Void> insertArticle(
            @Valid @RequestBody ArticleRequest request,
            @AuthenticationPrincipal SecurityUserAccount principal
    ) {
        articleCommandService.save(
                SaveArticleDto.builder()
                        .title(request.title())
                        .content(request.content())
                        .articleImagePath(request.articleImagePath())
                        .userId(principal.getAccount().getId())
                        .build()
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Article search(detail)",
            description = "게시글 상세 조회합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
            }
    )
    @GetMapping("/article/{id}")
    public ResponseEntity<CommonResponse<ArticleDetailDto>> getOne(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserAccount principal
    ) {
        ArticleDetailDto response = articleQueryService.findByIdDetail(id, principal.getAccount().getId());

        return ResponseEntity.ok(CommonResponse.of(Success.RETRIEVAL_POST, response));
    }

    @Operation(
            summary = "Article like toggle",
            description = "게시글 좋아요 상태를 토글합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ArticleLikeResponse.class))),
            }
    )
    @PostMapping("/article/{id}/like")
    public ResponseEntity<CommonResponse<ArticleLikeResponse>> toggleLike(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserAccount principal
    ) {
        boolean liked = articleLikeService.toggleLike(id, principal.getAccount().getId());

        return ResponseEntity.ok(
                CommonResponse.of(Success.ARTICLE_LIKE_UPDATED, ArticleLikeResponse.from(liked))
        );
    }

    @Operation(
            summary = "Article update",
            description = "게시글 수정합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공", content = @Content(schema = @Schema(implementation = ArticlePatchRequest.class))),
            }
    )
    @PatchMapping("/article/{id}")
    public ResponseEntity<Void> patchArticle(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ArticlePatchRequest request,
            @AuthenticationPrincipal SecurityUserAccount principal
    ) {
        articleCommandService.save(SaveArticleDto.of(id, request, principal.getAccount().getId()));

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Article Delete",
            description = "게시글 삭제합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
            }
    )
    @DeleteMapping("/article/{id}")
    public ResponseEntity<Void> deleteArticle(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id,
            @AuthenticationPrincipal SecurityUserAccount principal
    ) {
        Long userId = principal.getAccount().getId();
        articleCommandService.delete(userId, id);

        return ResponseEntity.noContent().build();
    }
}
