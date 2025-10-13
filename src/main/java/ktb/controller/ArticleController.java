package ktb.controller;

import jakarta.validation.Valid;

import ktb.common.pagination.Slice;
import ktb.dto.ArticleDto;
import ktb.dto.PageInfoDto;
import ktb.dto.SaveArticleDto;
import ktb.dto.request.AllArticleRetrieveRequest;
import ktb.dto.request.ArticlePatchRequest;
import ktb.dto.request.ArticleRequest;
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
    public ResponseEntity<ArticlesResponse<ArticleDto>> getAll(@Valid @RequestBody AllArticleRetrieveRequest request) {
        PageInfoDto pageInfo = PageInfoDto.of(request.after(), 10);

        Slice<ArticleDto> page = articleService.findAll(pageInfo);

        return ResponseEntity.ok(ArticlesResponse.of("", page.getData(), page.getPageInfo()));
    }

    @PostMapping("/article")
    public ResponseEntity<Void> insertArticle(@Valid @RequestBody ArticleRequest request) {
        articleService.save(SaveArticleDto.of(request));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<CommonResponse<ArticleDto>> getOne(@PathVariable Long id) {
        ArticleDto response = articleService.findById(id);

        return ResponseEntity.ok(CommonResponse.of("", response));
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
