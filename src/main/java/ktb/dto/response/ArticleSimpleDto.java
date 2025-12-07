package ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import ktb.domain.Article;

@Schema(description = "게시글 페이지 조회 시 각 게시글 별 응답 구조")
public record ArticleSimpleDto(
        @Schema(description = "게시글 ID", example = "1")
        @JsonProperty("id")
        Long id,

        @Schema(description = "게시글 제목", example = "게시글 제목")
        @JsonProperty("title")
        String title,

        @Schema(description = "게시글 작성한 유저 닉네임", example = "닉네임")
        @JsonProperty("user_nickname")
        String userNickname,

        @Schema(description = "좋아요 수", example = "0")
        @JsonProperty("like_cnt")
        int likeCnt,

        @Schema(description = "댓글 수", example = "0")
        @JsonProperty("comment_cnt")
        int commentCnt,

        @Schema(description = "조회 수", example = "1")
        @JsonProperty("view_cnt")
        int viewCnt,

        @Schema(description = "마지막 수정 일자", example = "2025-01-01T00:00:00")
        @JsonProperty("last_modified_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastModifiedDate
) {
    public static ArticleSimpleDto from(Article article) {
        LocalDateTime lastModified = article.getMeta().getUpdateAt() != null
                ? article.getMeta().getUpdateAt()
                : article.getMeta().getCreateAt();

        return new ArticleSimpleDto(
                article.getId(),
                article.getTitle(),
                article.getCreateBy().getNickname(),
                article.getMeta().getLikeCnt().get(),
                article.getComments().size(),
                article.getMeta().getViewCnt().get(),
                lastModified
        );
    }
}
