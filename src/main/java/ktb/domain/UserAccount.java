package ktb.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAccount {
    private Long id;
    private final String email;
    private final String nickName;
    private final String password;
    private final String profileImagePath;

    public void initId(Long id) {
        this.id = id;
    }
}
