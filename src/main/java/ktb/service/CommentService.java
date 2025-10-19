package ktb.service;

import ktb.dto.CommentDto;
import ktb.dto.UserAccountDto;
import ktb.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserService userService;
    private final ArticleRepository articleRepository;

    public CommentDto addComment(CommentDto request) {
        UserAccountDto user = userService.getUserInfo(request.createBy());
        
        return CommentDto.from(articleRepository.addComment(request.articleId(), request.content(), user.toEntity()));
    }

    public void update(CommentDto request) {
        // 인가는 @Authorized AOP 에서 확인
        articleRepository.updateComment(request.articleId(), request.id(), request.content());
    }

    public void delete(CommentDto request) {
        // 인가는 @Authorized AOP 에서 확인
        articleRepository.deleteComment(request.articleId(), request.id());
    }
}
