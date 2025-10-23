package ktb.handler.delete;

import ktb.domain.UserAccount;
import ktb.exception.article.AlreadyDeletedUser;
import ktb.handler.AbstractHandler;
import ktb.handler.context.SoftDeleteContext;
import ktb.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserSoftDeleteHandler extends AbstractHandler<SoftDeleteContext> {
    private final UserRepository userRepository;

    @Override
    public boolean handle(SoftDeleteContext context) {
        Long userId = context.traceId();

        UserAccount user = userRepository.findById(userId).orElseThrow(AlreadyDeletedUser::new);

        user.softDelete();

        //TODO: Save 실패 시 Rollback 가능하게 save 결과를 받아올 필요가 있음
        userRepository.save(user);

        return super.handle(context);
    }
}
