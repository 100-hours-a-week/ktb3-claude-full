package ktb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import ktb.domain.UserAccount;

public record UserAccountDto(
        @Schema(description = "유저 ID", example = "1")
        @JsonProperty("id")
        Long id,

        @Schema(description = "유저 이메일", example = "example@example.com")
        @JsonProperty("email")
        String email,

        @Schema(description = "유저 닉네임", example = "닉네임예시")
        @JsonProperty("nickname")
        String nickName,

        @Schema(description = "유저 프로필 이미지", example = "http://image.com/1")
        @JsonProperty("profile_image_path")
        String profileImagePath
) {
    public static UserAccountDto from(UserAccount userAccount) {
        return new UserAccountDto(userAccount.getId(), userAccount.getEmail(), userAccount.getNickname(),
                userAccount.getProfileImagePath());
    }
}
