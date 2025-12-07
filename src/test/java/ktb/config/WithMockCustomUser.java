package ktb.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 테스트에서 인증된 사용자를 시뮬레이션하는 커스텀 어노테이션
 *
 * <p>사용 예시:
 * <pre>
 * @Test
 * @WithMockCustomUser(userId = 1L, email = "test@test.com", nickname = "testUser")
 * void 사용자_조회_성공() {
 *     // 인증된 사용자로 테스트 실행
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    /**
     * 사용자 ID (기본값: 1L)
     */
    long userId() default 1L;

    /**
     * 이메일 (기본값: "test@test.com")
     */
    String email() default "test@test.com";

    /**
     * 닉네임 (기본값: "testUser")
     */
    String nickname() default "testUser";

    /**
     * 비밀번호 (기본값: "password")
     */
    String password() default "password";

    /**
     * 프로필 이미지 경로 (기본값: "/profile/test.jpg")
     */
    String profileImagePath() default "/profile/test.jpg";
}
