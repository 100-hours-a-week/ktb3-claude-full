package ktb.dto;

import ktb.domain.UserAccount;

public record SignUpUserDto(
        String email,
        String password,
        String nickName,
        String profileImagePath
) {
    public UserAccount toEntity() {
        return new UserAccount(null, email, password, nickName, profileImagePath);
    }
}
