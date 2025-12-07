package ktb.dto;

import ktb.domain.UserAccount;

public record SignUpUserDto(
        String email,
        String password,
        String nickName,
        String profileImagePath
) {
    public UserAccount toEntity(String encodedPassword) {
        return UserAccount.builder()
                .email(email)
                .nickname(nickName)
                .password(encodedPassword)
                .profileImagePath(profileImagePath)
                .build();
    }
}
