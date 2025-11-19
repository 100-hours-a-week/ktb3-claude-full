package ktb.auth.adapter;

import java.util.List;
import ktb.domain.UserAccount;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class SecurityUserAccount extends User {
    private final UserAccount account;
    public SecurityUserAccount(UserAccount account) {
        super(
                account.getId().toString(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        this.account = account;
    }
}
