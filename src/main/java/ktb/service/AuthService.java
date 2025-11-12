package ktb.service;

import ktb.domain.UserAccount;
import ktb.dto.UserAccountDto;
import ktb.exception.AuthenticateException;
import ktb.repository.UserRepository;

import ktb.util.BCryptEncoder;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public UserAccountDto authenticate(String email, String password) {
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(AuthenticateException::new);

        boolean passwordMatched = BCryptEncoder.matches(password, user.getPassword());

        if (!passwordMatched) {
            throw new AuthenticateException();
        }

        return UserAccountDto.from(user);
    }
}
