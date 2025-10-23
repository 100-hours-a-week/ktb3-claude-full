package ktb.service;

import ktb.domain.UserAccount;
import ktb.dto.SignUpUserDto;
import ktb.dto.UserAccountDto;
import ktb.dto.request.PasswordUpdateRequest;
import ktb.exception.AuthenticateException;
import ktb.exception.user.MisMatchPasswordException;
import ktb.exception.user.NonExistUserException;
import ktb.handler.AbstractHandler;
import ktb.handler.context.SoftDeleteContext;
import ktb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AbstractHandler<SoftDeleteContext> userDeleteHandlerChain;

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
        SoftDeleteContext context = new SoftDeleteContext(id, null);

        // Handler 체인을 통한 소프트 삭제 처리 (User -> Article -> Comment 순서)
        userDeleteHandlerChain.handle(context);
    }
}
