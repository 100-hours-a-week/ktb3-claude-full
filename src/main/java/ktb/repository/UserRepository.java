package ktb.repository;

import java.util.Optional;
import ktb.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findById(Long id);
    Optional<UserAccount> findByEmail(String email);
}
