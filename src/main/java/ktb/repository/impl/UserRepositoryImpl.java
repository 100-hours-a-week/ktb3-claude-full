package ktb.repository.impl;

import java.util.Optional;
import ktb.db.user.UserData;
import ktb.domain.UserAccount;
import ktb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
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
    public void delete(Long id) {
        UserData.deleteById(id);
    }

    @Override
    public boolean isExistUser(Long id) {
        return UserData.isExistUser(id);
    }
}
