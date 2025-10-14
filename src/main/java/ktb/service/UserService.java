package ktb.service;

import java.util.NoSuchElementException;
import ktb.domain.UserAccount;
import ktb.dto.SignUpUserDto;
import ktb.dto.UserAccountDto;
import ktb.exception.AuthenticateException;
import ktb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long signUp(SignUpUserDto user) {
        UserAccount saveUser = userRepository.save(user.toEntity());

        return saveUser.getId();
    }

    public UserAccountDto search(Long id) {
        return userRepository.findById(id)
                .map(UserAccountDto::from)
                .orElseThrow();
    }

    public void updateNickName(Long id, String nickName) {
        UserAccount existUser =
                userRepository.findById(id)
                        .orElseThrow(NoSuchElementException::new);

        existUser.changeNickName(nickName);

        userRepository.save(existUser);
    }

    public void updatePassword(Long id, String password) {
        UserAccount existUser =
                userRepository.findById(id)
                        .orElseThrow(NoSuchElementException::new);

        existUser.changePassword(password);

        userRepository.save(existUser);
    }

    public void delete(Long id) {
        UserAccount exist = userRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        userRepository.delete(id);
    }

    public void authentication(Long id) {
        if(!userRepository.isExistUser(id)) {
            throw new AuthenticateException();
        }
    }

    public UserAccountDto getUserInfo(Long id) {
        return UserAccountDto.from(userRepository.findById(id).orElseThrow(NoSuchElementException::new));
    }
}
