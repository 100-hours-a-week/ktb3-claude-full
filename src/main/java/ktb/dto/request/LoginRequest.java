package ktb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import ktb.constant.MessageConstant;
import ktb.constant.MessageConstant.Password;
import ktb.constant.RegexpConstant;

@Schema(description = "로그인 요청 구조")
public record LoginRequest(
        @Schema(description = "ID(Email)", example = "test@example.com")
        @Email(message = MessageConstant.Email.PATTERN)
        String email,

        @Schema(description = "패스워드", example = "Password12#$56")
        @Pattern(regexp = RegexpConstant.PASSWORD
                , message = Password.PATTERN)
        String password
) {
}
