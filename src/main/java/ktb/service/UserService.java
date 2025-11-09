package ktb.service;

import ktb.domain.UserAccount;
import ktb.dto.SignUpUserDto;
import ktb.dto.UserAccountDto;
import ktb.dto.request.PasswordUpdateRequest;
import ktb.exception.user.MisMatchPasswordException;
import ktb.exception.user.NonExistUserException;
import ktb.handler.AbstractHandler;
import ktb.handler.context.ContextData;
import ktb.handler.context.SoftDeleteContext;
import ktb.handler.context.payload.SoftDeletePayload;
import ktb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AbstractHandler<ContextData<?>> userDeleteHandlerChain;

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
        // User 전체 삭제: traceId에 userId, payload 는 articleId null 로 설정
        SoftDeleteContext context = new SoftDeleteContext(
                id,
                new SoftDeletePayload(id, null)
        );

        // Handler 체인을 통한 소프트 삭제 처리 (Authorization → Validation → Execution → Audit)
        // Execution 단계에서 UserDeleteStrategy, UserArticlesDeleteStrategy, UserCommentsDeleteStrategy가 순서대로 실행
        userDeleteHandlerChain.handle(context);
    }

    public boolean existNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean existEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
