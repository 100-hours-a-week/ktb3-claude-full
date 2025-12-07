package ktb.domain;

import ktb.exception.article.AlreadyDeletedUser;
import ktb.fixture.UserAccountFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserAccount 도메인 테스트")
class UserAccountTest {

    @Test
    @DisplayName("닉네임_변경_성공")
    void 닉네임_변경_성공() {
        // Given
        UserAccount user = UserAccountFixture.create("test@test.com", "oldNickname");
        String newNickname = "newNickname";

        // When
        user.changeNickName(newNickname);

        // Then
        assertThat(user.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("비밀번호_변경_성공")
    void 비밀번호_변경_성공() {
        // Given
        UserAccount user = UserAccountFixture.createDefault();
        String newPassword = "newPassword123!";

        // When
        user.changePassword(newPassword);

        // Then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("소프트_삭제_성공")
    void 소프트_삭제_성공() {
        // Given
        UserAccount user = UserAccountFixture.createDefault();

        // When
        user.softDelete();

        // Then
        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getDeleteAt()).isNotNull();
        assertThat(user.isDelete()).isTrue();
    }

    @Test
    @DisplayName("소프트_삭제_실패_이미_삭제된_사용자")
    void 소프트_삭제_실패_이미_삭제된_사용자() {
        // Given
        UserAccount user = UserAccountFixture.createDeleted();

        // When & Then
        assertThatThrownBy(user::softDelete)
                .isInstanceOf(AlreadyDeletedUser.class);
    }

    @Test
    @DisplayName("소프트_복구_성공")
    void 소프트_복구_성공() {
        // Given
        UserAccount user = UserAccountFixture.createDeleted();

        // When
        user.softRestore();

        // Then
        assertThat(user.isDeleted()).isFalse();
        assertThat(user.getDeleteAt()).isNull();
        assertThat(user.isDelete()).isFalse();
    }

    @Test
    @DisplayName("소프트_복구_실패_삭제되지_않은_사용자")
    void 소프트_복구_실패_삭제되지_않은_사용자() {
        // Given
        UserAccount user = UserAccountFixture.createDefault();

        // When & Then
        assertThatThrownBy(user::softRestore)
                .isInstanceOf(AlreadyDeletedUser.class);
    }

    @Test
    @DisplayName("삭제_여부_확인_삭제된_사용자")
    void 삭제_여부_확인_삭제된_사용자() {
        // Given
        UserAccount user = UserAccountFixture.createDeleted();

        // When & Then
        assertThat(user.isDelete()).isTrue();
    }

    @Test
    @DisplayName("삭제_여부_확인_활성_사용자")
    void 삭제_여부_확인_활성_사용자() {
        // Given
        UserAccount user = UserAccountFixture.createDefault();

        // When & Then
        assertThat(user.isDelete()).isFalse();
    }

    @Test
    @DisplayName("동등성_비교_같은_사용자")
    void 동등성_비교_같은_사용자() {
        // Given
        UserAccount user1 = UserAccountFixture.create(1L, "test@test.com", "testUser");
        UserAccount user2 = UserAccountFixture.create(1L, "test@test.com", "testUser");

        // When & Then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    @DisplayName("동등성_비교_다른_사용자")
    void 동등성_비교_다른_사용자() {
        // Given
        UserAccount user1 = UserAccountFixture.create(1L, "test1@test.com", "user1");
        UserAccount user2 = UserAccountFixture.create(2L, "test2@test.com", "user2");

        // When & Then
        assertThat(user1).isNotEqualTo(user2);
    }
}
