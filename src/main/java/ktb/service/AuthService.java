package ktb.service;

import ktb.domain.UserAccount;
import ktb.dto.UserAccountDto;
import ktb.exception.AuthenticateException;
import ktb.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public UserAccountDto authenticate(String email, String password) {
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(AuthenticateException::new);

        if (!password.equals(user.getPassword())) {
            throw new AuthenticateException();
        }

        return UserAccountDto.from(user);
    }
}
