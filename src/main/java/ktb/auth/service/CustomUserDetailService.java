package ktb.auth.service;

import java.util.Collections;
import ktb.domain.UserAccount;
import ktb.exception.user.NonExistUserException;
import ktb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount =
                userRepository.findByEmail(username)
                        .orElseThrow(NonExistUserException::new);

        return new org.springframework.security.core.userdetails.User(
                userAccount.getEmail(),
                userAccount.getPassword(),
                Collections.emptyList()
        );
    }
}
