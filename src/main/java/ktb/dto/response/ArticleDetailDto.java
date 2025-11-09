package ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import ktb.domain.Article;

import java.util.List;

@Schema(description = "게시글 상세 조회 응답 구조")
public record ArticleDetailDto(
        @Schema(description = "게시글 ID", example = "1")
        @JsonProperty("id")
        String id,

        @Schema(description = "게시글 제목", example = "게시글 제목")
        @JsonProperty("title")
        String title,

        @Schema(description = "게시글 내용", example = "게시글 내용")
        @JsonProperty("content")
        String content,

        @Schema(description = "게시글 작성한 유저 닉네임", example = "닉네임")
        @JsonProperty("user_nickname")
        String userNickname,

        @Schema(description = "좋아요 수", example = "0")
        @JsonProperty("like_cnt")
        String likeCnt,

        @Schema(description = "댓글 수", example = "0")
        @JsonProperty("comment_cnt")
        String commentCnt,

        @Schema(description = "조회 수", example = "1")
        @JsonProperty("view_cnt")
        String viewCnt,

        @Schema(description = "마지막 수정 일자", example = "2025-01-01T00:00:00")
        @JsonProperty("last_modified_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastModifiedDate,

        @Schema(description = "수정 가능 여부", example = "true")
        @JsonProperty("can_modify")
        Boolean canModify,

        @Schema(description = "댓글 목록", implementation = CommentDetailDto.class)
        @JsonProperty("comment")
        List<CommentDetailDto> comment
) {
    public static ArticleDetailDto from(Article article, Long userId) {
        LocalDateTime lastModified = article.getMeta().getUpdateAt() != null
                ? article.getMeta().getUpdateAt()
                : article.getMeta().getCreateAt();
        boolean canModify = article.getCreateBy().getId().equals(userId);

        return new ArticleDetailDto(
                String.valueOf(article.getId()),
                article.getTitle(),
                article.getContent(),
                article.getCreateBy().getNickname(),
                String.valueOf(article.getMeta().getLikeCnt().get()),
                String.valueOf(article.getComments().size()),
                String.valueOf(article.getMeta().getViewCnt().get()),
                lastModified,
                canModify,
                article.getComments().stream()
                        .map(comment -> CommentDetailDto.from(comment, userId))
                        .toList()
        );
    }
}
