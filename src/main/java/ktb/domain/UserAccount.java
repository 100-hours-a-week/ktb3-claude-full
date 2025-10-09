package ktb.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAccount {
    private final Long userAccountSeq;
    private final String email;
    private final String nickName;
    private final String password;
    private final String profileImagePath;
}
