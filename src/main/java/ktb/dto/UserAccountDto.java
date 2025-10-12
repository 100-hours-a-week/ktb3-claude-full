package ktb.dto;

import ktb.domain.UserAccount;
import lombok.Getter;

@Getter
public record UserAccountDto(
        Long id,
        String email,
        String nickName,
        String profileImagePath
) {
    public static UserAccountDto from(UserAccount userAccount) {
        return new UserAccountDto(userAccount.getId(), userAccount.getEmail(), userAccount.getNickName(),
                userAccount.getProfileImagePath());
    }
}
