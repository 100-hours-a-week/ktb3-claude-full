package ktb.auth.service;

import ktb.auth.adapter.SecurityUserAccount;
import ktb.domain.UserAccount;
import ktb.exception.AuthenticateException;
import ktb.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount userAccount = userRepository
                .findByEmail(email)
                .orElseThrow(AuthenticateException::new);
        return new SecurityUserAccount(userAccount);
    }

    /**
     * userId로 사용자 조회 (JWT 인증 시 JwtAuthenticationFilter 호출)
     */
    public UserDetails loadUserById(Long userId) {
        UserAccount userAccount = userRepository
                .findById(userId)
                .orElseThrow(AuthenticateException::new);
        return new SecurityUserAccount(userAccount);
    }
}
