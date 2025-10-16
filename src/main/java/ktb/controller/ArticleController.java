package ktb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import ktb.common.pagination.Slice;
import ktb.constant.MessageConstant.Success;
import ktb.domain.Article;
import ktb.dto.ArticleDto;
import ktb.dto.PageInfoDto;
import ktb.dto.SaveArticleDto;
import ktb.dto.request.AllArticleRetrieveRequest;
import ktb.dto.request.ArticlePatchRequest;
import ktb.dto.request.ArticleRequest;
import ktb.dto.response.ArticleDetailDto;
import ktb.dto.response.ArticleSimpleDto;
import ktb.dto.response.ArticlesResponse;
import ktb.dto.response.CommonResponse;
import ktb.service.ArticleService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Article", description = "게시글 리소스 관련 API")
@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @Operation(
            summary = "Article search(page)",
            description = "게시글들의 페이지 조회합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = AllArticleRetrieveRequest.class))),
            }
    )
    @GetMapping("/articles")
    public ResponseEntity<ArticlesResponse<ArticleSimpleDto>> getAll(@Valid @RequestBody AllArticleRetrieveRequest request) {
        PageInfoDto pageInfo = PageInfoDto.of(request.after(), request.limit());

        Slice<ArticleSimpleDto> page = articleService.findAll(pageInfo);

        return ResponseEntity.ok(ArticlesResponse.of(Success.RETRIEVAL_ALL, page.getData(), page.getPageInfo()));
    }

    @Operation(
            summary = "Article insert",
            description = "게시글 단건 등록합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ArticleRequest.class))),
            }
    )
    @PostMapping("/article")
    public ResponseEntity<Void> insertArticle(@Valid @RequestBody ArticleRequest request) {
        articleService.save(SaveArticleDto.of(request));

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
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id
    ) {
        ArticleDetailDto response = articleService.findByIdDetail(id);

        return ResponseEntity.ok(CommonResponse.of(Success.RETRIEVAL_POST, response));
    }

    @Operation(
            summary = "Article update",
            description = "게시글 수정합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ArticlePatchRequest.class))),
            }
    )
    @PatchMapping("/article/{id}")
    public ResponseEntity<Void> patchArticle(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ArticlePatchRequest request
    ) {
        articleService.save(SaveArticleDto.of(id, request));

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Article Delete",
            description = "게시글 삭제합니다.",
            tags = { "Article" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
            }
    )
    @DeleteMapping("/article/{id}")
    public ResponseEntity<Void> deleteArticle(
            @Parameter(name = "id", description = "게시글 ID", required = true) @PathVariable Long id
    ) {
        articleService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
