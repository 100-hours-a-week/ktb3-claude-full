package ktb.service;

import ktb.domain.UserAccount;
import ktb.dto.SignUpUserDto;
import ktb.dto.UserAccountDto;
import ktb.dto.request.PasswordUpdateRequest;
import ktb.exception.article.AlreadyDeletedUser;
import ktb.exception.user.MisMatchPasswordException;
import ktb.exception.user.NonExistUserException;
import ktb.fixture.UserAccountFixture;
import ktb.handler.AbstractHandler;
import ktb.handler.context.ContextData;
import ktb.handler.context.SoftDeleteContext;
import ktb.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AbstractHandler<ContextData<?>> userDeleteHandlerChain;

    @Mock
    private PasswordEncoder encoder;

    @Test
    @DisplayName("사용자_삭제_시_핸들러_체인_호출됨")
    void 사용자_삭제_시_핸들러_체인_호출됨() {
        // Given
        Long userId = 1L;
        when(userDeleteHandlerChain.handle(any(SoftDeleteContext.class))).thenReturn(true);

        // When
        userService.delete(userId);

        // Then
        verify(userDeleteHandlerChain).handle(any(SoftDeleteContext.class));
    }

    @Test
    @DisplayName("사용자_삭제_시_올바른_컨텍스트로_핸들러_호출됨")
    void 사용자_삭제_시_올바른_컨텍스트로_핸들러_호출됨() {
        // Given
        Long userId = 5L;
        ArgumentCaptor<SoftDeleteContext> contextCaptor = ArgumentCaptor.forClass(SoftDeleteContext.class);
        when(userDeleteHandlerChain.handle(any(SoftDeleteContext.class))).thenReturn(true);

        // When
        userService.delete(userId);

        // Then
        verify(userDeleteHandlerChain).handle(contextCaptor.capture());
        SoftDeleteContext capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.traceId()).isEqualTo(userId);
        assertThat(capturedContext.payload().userId()).isEqualTo(userId);
        assertThat(capturedContext.payload().articleId()).isNull();
    }

    @Test
    @DisplayName("사용자_ID_존재_여부_확인_성공")
    void 사용자_ID_존재_여부_확인_성공() {
        // Given
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // When
        boolean exists = userService.existId(userId);

        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsById(userId);
    }

    @Test
    @DisplayName("사용자_닉네임_존재_여부_확인_성공")
    void 사용자_닉네임_존재_여부_확인_성공() {
        // Given
        String nickname = "testUser";
        when(userRepository.existsByNickname(nickname)).thenReturn(true);

        // When
        boolean exists = userService.existNickname(nickname);

        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByNickname(nickname);
    }

    @Test
    @DisplayName("사용자_이메일_존재_여부_확인_성공")
    void 사용자_이메일_존재_여부_확인_성공() {
        // Given
        String email = "test@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean exists = userService.existEmail(email);

        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("사용자_회원가입_성공")
    void 사용자_회원가입_성공() {
        // Given
        SignUpUserDto signUpDto = new SignUpUserDto(
                "test@test.com",
                "password",
                "testUser",
                "profileImage"
        );
        UserAccount savedUser = UserAccountFixture.createWithId(1L);

        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserAccount.class))).thenReturn(savedUser);

        // When
        Long savedId = userService.signUp(signUpDto);

        // Then
        assertThat(savedId).isEqualTo(1L);
        verify(encoder).encode("password");
        verify(userRepository).save(any(UserAccount.class));
    }

    @Test
    @DisplayName("사용자_조회_성공")
    void 사용자_조회_성공() {
        // Given
        Long userId = 1L;
        UserAccount user = UserAccountFixture.create(userId, "test@test.com", "testUser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserAccountDto result = userService.search(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("test@test.com");
        assertThat(result.nickName()).isEqualTo("testUser");
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("사용자_조회_실패_존재하지_않는_사용자")
    void 사용자_조회_실패_존재하지_않는_사용자() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.search(userId))
                .isInstanceOf(NonExistUserException.class);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("사용자_조회_실패_삭제된_사용자")
    void 사용자_조회_실패_삭제된_사용자() {
        // Given
        Long userId = 1L;
        UserAccount deletedUser = UserAccountFixture.createDeleted();
        when(userRepository.findById(userId)).thenReturn(Optional.of(deletedUser));

        // When & Then
        assertThatThrownBy(() -> userService.search(userId))
                .isInstanceOf(AlreadyDeletedUser.class);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("사용자_닉네임_수정_성공")
    void 사용자_닉네임_수정_성공() {
        // Given
        Long userId = 1L;
        String newNickname = "newNickname";
        UserAccount user = UserAccountFixture.createWithId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.updateNickName(userId, newNickname);

        // Then
        assertThat(user.getNickname()).isEqualTo(newNickname);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("사용자_닉네임_수정_실패_존재하지_않는_사용자")
    void 사용자_닉네임_수정_실패_존재하지_않는_사용자() {
        // Given
        Long userId = 999L;
        String newNickname = "newNickname";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateNickName(userId, newNickname))
                .isInstanceOf(NonExistUserException.class);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("사용자_비밀번호_수정_성공")
    void 사용자_비밀번호_수정_성공() {
        // Given
        Long userId = 1L;
        PasswordUpdateRequest request = new PasswordUpdateRequest("newPassword", "newPassword");
        UserAccount user = UserAccountFixture.createWithId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(encoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // When
        userService.updatePassword(userId, request);

        // Then
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        verify(userRepository).findById(userId);
        verify(encoder).encode("newPassword");
    }

    @Test
    @DisplayName("사용자_비밀번호_수정_실패_비밀번호_불일치")
    void 사용자_비밀번호_수정_실패_비밀번호_불일치() {
        // Given
        Long userId = 1L;
        PasswordUpdateRequest request = new PasswordUpdateRequest("newPassword", "differentPassword");

        // When & Then
        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(MisMatchPasswordException.class);
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("사용자_비밀번호_수정_실패_존재하지_않는_사용자")
    void 사용자_비밀번호_수정_실패_존재하지_않는_사용자() {
        // Given
        Long userId = 999L;
        PasswordUpdateRequest request = new PasswordUpdateRequest("newPassword", "newPassword");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(NonExistUserException.class);
        verify(userRepository).findById(userId);
        verify(encoder, never()).encode(anyString());
    }
}
