package ktb.repository;

import java.util.Optional;
import ktb.db.user.UserData;
import ktb.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;

@Primary
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return UserData.findByEmail(email);
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return UserData.findById(id);
    }

    @Override
    public UserAccount save(UserAccount user) {
        UserData.save(user);
        return user;
    }

    @Override
    public void update(UserAccount originUser, UserAccount updateUser) {
        UserData.update(originUser, updateUser);
    }

    @Override
    public void delete(Long id) {
        UserData.deleteById(id);
    }
}
