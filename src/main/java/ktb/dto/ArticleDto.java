package ktb.dto;

import java.util.List;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleDto {
    private final String title;
    private final String content;
    private final UserAccount user;
    private final List<ArticleComment> comments;

    public Article from(Long id) {
        return Article.create(id, this.title, this.content, this.user);
    }
}
