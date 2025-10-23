package ktb.dto;

import ktb.domain.UserAccount;

public record SignUpUserDto(
        String email,
        String password,
        String nickName,
        String profileImagePath
) {
    public UserAccount toEntity() {
        return UserAccount.builder()
                .email(email)
                .nickName(nickName)
                .password(password)
                .profileImagePath(profileImagePath)
                .build();
    }
}
