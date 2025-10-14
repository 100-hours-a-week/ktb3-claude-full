package ktb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import ktb.constant.MessageConstant.Password;
import ktb.constant.RegexpConstant;

public record PasswordUpdateRequest(
        @NotBlank(message = Password.REQUIRED)
        @Pattern(regexp = RegexpConstant.PASSWORD
                , message = Password.PATTERN)
        String password,

        @NotBlank(message = Password.CONFIRMATION_REQUIRED)
        String confirmPassword
) {
}
