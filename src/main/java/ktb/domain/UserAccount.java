package ktb.domain;

import java.util.Objects;
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


    public UserAccount changeNickName(String nickName) {
        return new UserAccount(this.id, this.email, nickName, this.password, this.profileImagePath);
    }

    public UserAccount changePassword(String password) {
        return new UserAccount(this.id, this.email, this.nickName, password, this.profileImagePath);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof UserAccount)) {
            return false;
        }

        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email)
                && Objects.equals(nickName, that.nickName) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
