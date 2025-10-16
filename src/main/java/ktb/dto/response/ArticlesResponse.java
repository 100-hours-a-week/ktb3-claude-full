package ktb.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import ktb.common.pagination.PageInfo;

@Schema(description = "게시글 페이지 응답 구조")
public record ArticlesResponse<T>(
        @Schema(description = "실행 결과 message")
        @NotNull
        String message,

        @Schema(description = "게시글 목록", implementation = ArticleSimpleDto.class)
        Collection<T> data,

        @Schema(description = "Page 정보", implementation = PageInfo.class)
        @NotNull
        PageInfo pageInfo
) {
        public static <T> ArticlesResponse<T> of(String message, Collection<T> data, PageInfo pageInfo) {
                return new ArticlesResponse<>(message, data, pageInfo);
        }
}
