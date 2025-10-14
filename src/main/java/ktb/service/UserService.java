package ktb.service;

import ktb.domain.UserAccount;
import ktb.dto.SignUpUserDto;
import ktb.dto.UserAccountDto;
import ktb.dto.request.PasswordUpdateRequest;
import ktb.exception.AuthenticateException;
import ktb.exception.user.MisMatchPasswordException;
import ktb.exception.user.NonExistUserException;
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
                .orElseThrow(NonExistUserException::new);
    }

    public void updateNickName(Long id, String nickName) {
        UserAccount existUser =
                userRepository.findById(id)
                        .orElseThrow(NonExistUserException::new);

        existUser.changeNickName(nickName);

        userRepository.save(existUser);
    }

    public void updatePassword(Long id, PasswordUpdateRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new MisMatchPasswordException();
        }
        UserAccount existUser =
                userRepository.findById(id)
                        .orElseThrow(NonExistUserException::new);

        existUser.changePassword(request.password());

        userRepository.save(existUser);
    }

    public void delete(Long id) {
        UserAccount exist = userRepository.findById(id)
                .orElseThrow(NonExistUserException::new);

        userRepository.delete(id);
    }

    public void authentication(Long id) {
        if(!userRepository.isExistUser(id)) {
            throw new AuthenticateException();
        }
    }

    public UserAccountDto getUserInfo(Long id) {
        return UserAccountDto.from(userRepository.findById(id).orElseThrow(NonExistUserException::new));
    }
}
