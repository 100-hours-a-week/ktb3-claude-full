package ktb.controller;

import jakarta.validation.Valid;
import ktb.dto.ArticleDto;
import ktb.dto.request.AllArticleRetrieveRequest;
import ktb.dto.response.ArticlesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/articles")
    public ResponseEntity<ArticlesResponse<ArticleDto>> getAll(@Valid @RequestBody AllArticleRetrieveRequest request) {

    }
}
