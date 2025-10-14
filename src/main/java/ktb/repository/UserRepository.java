package ktb.repository;

import java.util.Optional;
import ktb.domain.UserAccount;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository {
    Optional<UserAccount> findById(Long id);
    Optional<UserAccount> findByEmail(String email);
    UserAccount save(UserAccount user);
    void delete(Long id);
    boolean isExistUser(Long id);
}
