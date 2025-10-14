package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import ktb.constant.MessageConstant;
import ktb.constant.MessageConstant.Common;
import ktb.constant.MessageConstant.Nickname;
import ktb.constant.MessageConstant.Password;
import ktb.constant.MessageConstant.UserImage;
import ktb.constant.RegexpConstant;
import ktb.dto.SignUpUserDto;

public record SignupRequest (
        @NotNull(message = MessageConstant.Email.REQUIRED)
        @Email(message = MessageConstant.Email.PATTERN)
        String email,

        @NotNull(message = Password.REQUIRED)
        @Pattern(regexp = RegexpConstant.PASSWORD
                , message = Password.PATTERN)
        String password,

        @NotNull(message = Nickname.REQUIRED)
        @NotBlank(message = Common.NO_WHITESPACE_ALLOWED)
        @Size(max = 10, message = Nickname.LENGTH_EXCEEDED)
        String nickname,

        @NotNull(message = UserImage.REQUIRED)
        @JsonProperty("profile_image_path")
        String profileImagePath
) {
        public SignUpUserDto from() {
                return new SignUpUserDto(email, password, nickname, profileImagePath);
        }
}
