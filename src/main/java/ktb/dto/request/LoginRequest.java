package ktb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import ktb.constant.MessageConstant;
import ktb.constant.MessageConstant.Password;
import ktb.constant.RegexpConstant;

public record LoginRequest(
        @Email(message = MessageConstant.Email.PATTERN)
        String email,

        @Pattern(regexp = RegexpConstant.PASSWORD
                , message = Password.PATTERN)
        String password
) {
}
