package ktb.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import ktb.constant.MessageConstant.Nickname;

public record NickNameUpdateRequest(
        @NotNull(message = Nickname.REQUIRED)
        @Size(max = 10, message = Nickname.LENGTH_EXCEEDED)
        String nickName
) {
}
