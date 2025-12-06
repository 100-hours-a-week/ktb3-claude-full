package ktb.config;

import ktb.auth.adapter.SecurityUserAccount;
import ktb.domain.UserAccount;
import ktb.fixture.UserAccountFixture;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * @WithMockCustomUser 어노테이션을 처리하여 SecurityContext를 생성하는 팩토리 클래스
 *
 * <p>테스트 실행 시 SecurityContext에 SecurityUserAccount를 주입하여
 * 실제 인증된 사용자처럼 동작하도록 만듭니다.
 */
public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Annotation에서 값을 읽어 UserAccount 생성
        UserAccount userAccount = UserAccount.builder()
                .id(annotation.userId())
                .email(annotation.email())
                .nickname(annotation.nickname())
                .password(annotation.password())
                .profileImagePath(annotation.profileImagePath())
                .isDeleted(false)
                .build();

        // SecurityUserAccount로 래핑
        SecurityUserAccount principal = new SecurityUserAccount(userAccount);

        // Authentication 객체 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                principal.getPassword(),
                principal.getAuthorities()
        );

        context.setAuthentication(auth);
        return context;
    }
}
