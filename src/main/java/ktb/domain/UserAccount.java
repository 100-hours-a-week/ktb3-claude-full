package ktb.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import ktb.exception.article.AlreadyDeletedUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAccount {
    private Long id;
    private final String email;
    private String nickName;
    private String password;
    private String profileImagePath;
    private boolean isDeleted;
    private LocalDateTime deleteAt;

    public void initId(Long id) {
        this.id = id;
    }


    public void changeNickName(String nickName) {
        this.nickName = nickName;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void softDelete() {
        if(!isDeleted) {
            isDeleted = Boolean.TRUE;
            deleteAt = LocalDateTime.now();

            return;
        }

        throw new AlreadyDeletedUser();
    }

    public void softRestore() {
        if (isDeleted) {
            isDeleted = Boolean.FALSE;
            deleteAt = null;

            return;
        }

        throw new AlreadyDeletedUser();
    }

    public boolean isDelete() {
        return (isDeleted == Boolean.TRUE) && (deleteAt != null);
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
