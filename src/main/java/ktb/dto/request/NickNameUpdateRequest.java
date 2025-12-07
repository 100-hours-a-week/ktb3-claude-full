package ktb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import ktb.constant.MessageConstant.Nickname;

@Schema(description = "닉네임 변경 요청 구조")
public record NickNameUpdateRequest(
        @Schema(description = "변경할 닉네임",example = "nickname12")
        @NotNull(message = Nickname.REQUIRED)
        @Size(max = 10, message = Nickname.LENGTH_EXCEEDED)
        String nickName
) {
}
