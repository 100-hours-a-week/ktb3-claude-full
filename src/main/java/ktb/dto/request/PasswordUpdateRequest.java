package ktb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import ktb.constant.MessageConstant.Password;
import ktb.constant.RegexpConstant;

@Schema(description = "패스워드 변경 요청 구조")
public record PasswordUpdateRequest(
        @Schema(description = "패스워드", example = "Password12#$56")
        @NotBlank(message = Password.REQUIRED)
        @Pattern(regexp = RegexpConstant.PASSWORD
                , message = Password.PATTERN)
        String password,

        @Schema(description = "확인 용도 패스워드", example = "Password12#$56")
        @NotBlank(message = Password.CONFIRMATION_REQUIRED)
        String confirmPassword
) {
}
