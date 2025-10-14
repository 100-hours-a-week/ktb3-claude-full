package ktb.controller;

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

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/articles")
    public ResponseEntity<ArticlesResponse<ArticleSimpleDto>> getAll(@Valid @RequestBody AllArticleRetrieveRequest request) {
        PageInfoDto pageInfo = PageInfoDto.of(request.after(), request.limit());

        Slice<ArticleSimpleDto> page = articleService.findAll(pageInfo);

        return ResponseEntity.ok(ArticlesResponse.of(Success.RETRIEVAL_ALL, page.getData(), page.getPageInfo()));
    }

    @PostMapping("/article")
    public ResponseEntity<Void> insertArticle(@Valid @RequestBody ArticleRequest request) {
        articleService.save(SaveArticleDto.of(request));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<CommonResponse<ArticleDetailDto>> getOne(@PathVariable Long id) {
        ArticleDetailDto response = articleService.findByIdDetail(id);

        return ResponseEntity.ok(CommonResponse.of(Success.RETRIEVAL_POST, response));
    }

    @PatchMapping("/article/{id}")
    public ResponseEntity<Void> patchArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticlePatchRequest request
    ) {
        articleService.save(SaveArticleDto.of(id, request));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/article/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
