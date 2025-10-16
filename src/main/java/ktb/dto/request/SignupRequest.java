package ktb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "회원가입 요청 구조")
public record SignupRequest (
        @Schema(description = "ID(Email)", example = "test@example.com")
        @NotNull(message = MessageConstant.Email.REQUIRED)
        @Email(message = MessageConstant.Email.PATTERN)
        String email,

        @Schema(description = "패스워드", example = "Password12#$56")
        @NotNull(message = Password.REQUIRED)
        @Pattern(regexp = RegexpConstant.PASSWORD
                , message = Password.PATTERN)
        String password,

        @Schema(description = "닉네임",example = "nickname12")
        @NotNull(message = Nickname.REQUIRED)
        @NotBlank(message = Common.NO_WHITESPACE_ALLOWED)
        @Size(max = 10, message = Nickname.LENGTH_EXCEEDED)
        String nickname,

        @Schema(description = "프로필 이미지 경로", example = "https://image.com/image.png")
        @NotNull(message = UserImage.REQUIRED)
        @JsonProperty("profile_image_path")
        String profileImagePath
) {
        public SignUpUserDto from() {
                return new SignUpUserDto(email, password, nickname, profileImagePath);
        }
}
