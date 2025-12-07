package ktb.fixture;

import ktb.domain.UserAccount;

/**
 * UserAccount 테스트 픽스처
 * 테스트에서 사용할 UserAccount 객체를 쉽게 생성하기 위한 유틸리티 클래스
 *
 * <p>사용 예시:
 * <pre>
 * // 기본 사용자
 * UserAccount user = UserAccountFixture.createDefault();
 *
 * // 특정 ID를 가진 사용자
 * UserAccount user = UserAccountFixture.createWithId(1L);
 *
 * // 이메일과 닉네임을 지정한 사용자
 * UserAccount user = UserAccountFixture.create("user@test.com", "nickname");
 * </pre>
 */
public class UserAccountFixture {

    private static final String DEFAULT_EMAIL = "test@test.com";
    private static final String DEFAULT_NICKNAME = "testUser";
    private static final String DEFAULT_PASSWORD = "password123!";
    private static final String DEFAULT_PROFILE_IMAGE = "/images/default-profile.jpg";

    /**
     * 기본 UserAccount 생성
     * ID는 null (JPA가 생성), isDeleted는 false
     */
    public static UserAccount createDefault() {
        return UserAccount.builder()
                .email(DEFAULT_EMAIL)
                .nickname(DEFAULT_NICKNAME)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(DEFAULT_PROFILE_IMAGE)
                .isDeleted(false)
                .build();
    }

    /**
     * 특정 ID를 가진 UserAccount 생성
     *
     * @param id 사용자 ID
     * @return UserAccount
     */
    public static UserAccount createWithId(Long id) {
        return UserAccount.builder()
                .id(id)
                .email(DEFAULT_EMAIL)
                .nickname(DEFAULT_NICKNAME)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(DEFAULT_PROFILE_IMAGE)
                .isDeleted(false)
                .build();
    }

    /**
     * 이메일과 닉네임을 지정한 UserAccount 생성
     *
     * @param email    이메일
     * @param nickname 닉네임
     * @return UserAccount
     */
    public static UserAccount create(String email, String nickname) {
        return UserAccount.builder()
                .email(email)
                .nickname(nickname)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(DEFAULT_PROFILE_IMAGE)
                .isDeleted(false)
                .build();
    }

    /**
     * ID, 이메일, 닉네임을 지정한 UserAccount 생성
     *
     * @param id       사용자 ID
     * @param email    이메일
     * @param nickname 닉네임
     * @return UserAccount
     */
    public static UserAccount create(Long id, String email, String nickname) {
        return UserAccount.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(DEFAULT_PROFILE_IMAGE)
                .isDeleted(false)
                .build();
    }

    /**
     * 특정 이메일을 가진 UserAccount 생성
     *
     * @param email 이메일
     * @return UserAccount
     */
    public static UserAccount createWithEmail(String email) {
        return UserAccount.builder()
                .email(email)
                .nickname(DEFAULT_NICKNAME)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(DEFAULT_PROFILE_IMAGE)
                .isDeleted(false)
                .build();
    }

    /**
     * 특정 닉네임을 가진 UserAccount 생성
     *
     * @param nickname 닉네임
     * @return UserAccount
     */
    public static UserAccount createWithNickname(String nickname) {
        return UserAccount.builder()
                .email(DEFAULT_EMAIL)
                .nickname(nickname)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(DEFAULT_PROFILE_IMAGE)
                .isDeleted(false)
                .build();
    }

    /**
     * 특정 ID와 이메일을 가진 UserAccount 생성
     *
     * @param id    사용자 ID
     * @param email 이메일
     * @return UserAccount
     */
    public static UserAccount createWithIdAndEmail(Long id, String email) {
        return UserAccount.builder()
                .id(id)
                .email(email)
                .nickname(DEFAULT_NICKNAME)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(DEFAULT_PROFILE_IMAGE)
                .isDeleted(false)
                .build();
    }

    /**
     * 삭제된 UserAccount 생성
     *
     * @return 삭제된 UserAccount
     */
    public static UserAccount createDeleted() {
        UserAccount user = UserAccount.builder()
                .email(DEFAULT_EMAIL)
                .nickname(DEFAULT_NICKNAME)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(DEFAULT_PROFILE_IMAGE)
                .isDeleted(false)
                .build();

        user.softDelete();
        return user;
    }

    /**
     * 프로필 이미지가 없는 UserAccount 생성
     *
     * @return UserAccount
     */
    public static UserAccount createWithoutProfileImage() {
        return UserAccount.builder()
                .email(DEFAULT_EMAIL)
                .nickname(DEFAULT_NICKNAME)
                .password(DEFAULT_PASSWORD)
                .profileImagePath(null)
                .isDeleted(false)
                .build();
    }
}
