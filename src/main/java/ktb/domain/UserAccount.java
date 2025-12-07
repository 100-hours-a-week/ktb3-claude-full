package ktb.domain;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import ktb.exception.article.AlreadyDeletedUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private String profileImagePath;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column
    private LocalDateTime deleteAt;

    public void changeNickName(String nickName) {
        this.nickname = nickName;
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
                && Objects.equals(nickname, that.nickname) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
