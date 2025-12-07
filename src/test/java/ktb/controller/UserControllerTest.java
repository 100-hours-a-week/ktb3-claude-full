package ktb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ktb.auth.filter.JwtAuthenticationFilter;
import ktb.config.TestSecurityConfig;
import ktb.config.WithMockCustomUser;
import ktb.dto.SignUpUserDto;
import ktb.dto.UserAccountDto;
import ktb.dto.request.DuplicateEmailRequest;
import ktb.dto.request.DuplicateNicknameRequest;
import ktb.dto.request.NickNameUpdateRequest;
import ktb.dto.request.PasswordUpdateRequest;
import ktb.dto.request.SignupRequest;
import ktb.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@Import(TestSecurityConfig.class)
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("회원가입_성공")
    void 회원가입_성공() throws Exception {
        // Given
        SignupRequest request = new SignupRequest(
                "test@test.com",
                "Password123!",
                "testUser",
                "/profile/test.jpg"
        );
        Long userId = 1L;

        when(userService.signUp(any(SignUpUserDto.class))).thenReturn(userId);

        // When & Then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/user/login"))
                .andExpect(jsonPath("$.data").value(1));

        verify(userService).signUp(any(SignUpUserDto.class));
    }

    @Test
    @DisplayName("회원가입_실패_이메일_형식_오류")
    void 회원가입_실패_이메일_형식_오류() throws Exception {
        // Given
        SignupRequest request = new SignupRequest(
                "invalid-email",
                "Password123!",
                "testUser",
                "/profile/test.jpg"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입_실패_닉네임_공백")
    void 회원가입_실패_닉네임_공백() throws Exception {
        // Given
        SignupRequest request = new SignupRequest(
                "test@test.com",
                "Password123!",
                "  ",
                "/profile/test.jpg"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자_조회_성공")
    @WithMockCustomUser(userId = 1L, email = "test@test.com", nickname = "testUser")
    void 사용자_조회_성공() throws Exception {
        // Given
        UserAccountDto userDto = new UserAccountDto(
                1L,
                "test@test.com",
                "testUser",
                "/profile/test.jpg"
        );

        when(userService.search(anyLong())).thenReturn(userDto);

        // When & Then
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("testUser"));

        verify(userService).search(1L);
    }

    @Test
    @DisplayName("닉네임_수정_성공")
    @WithMockCustomUser(userId = 1L, email = "test@test.com", nickname = "oldNickname")
    void 닉네임_수정_성공() throws Exception {
        // Given
        NickNameUpdateRequest request = new NickNameUpdateRequest("newNick");

        doNothing().when(userService).updateNickName(anyLong(), anyString());

        // When & Then
        mockMvc.perform(patch("/api/v1/users/me/nickName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).updateNickName(1L, "newNick");
    }

    @Test
    @DisplayName("닉네임_수정_실패_닉네임_길이_초과")
    @WithMockCustomUser
    void 닉네임_수정_실패_닉네임_길이_초과() throws Exception {
        // Given
        NickNameUpdateRequest request = new NickNameUpdateRequest("thisIsVeryLongNicknameExceedingLimit");

        // When & Then
        mockMvc.perform(patch("/api/v1/users/me/nickName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호_수정_성공")
    @WithMockCustomUser(userId = 1L)
    void 비밀번호_수정_성공() throws Exception {
        // Given
        PasswordUpdateRequest request = new PasswordUpdateRequest("NewPassword123!", "NewPassword123!");

        doNothing().when(userService).updatePassword(anyLong(), any(PasswordUpdateRequest.class));

        // When & Then
        mockMvc.perform(patch("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).updatePassword(1L, request);
    }

    @Test
    @DisplayName("비밀번호_수정_실패_비밀번호_형식_오류")
    @WithMockCustomUser
    void 비밀번호_수정_실패_비밀번호_형식_오류() throws Exception {
        // Given
        PasswordUpdateRequest request = new PasswordUpdateRequest("weak", "weak");

        // When & Then
        mockMvc.perform(patch("/api/v1/users/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("사용자_삭제_성공")
    @WithMockCustomUser(userId = 1L)
    void 사용자_삭제_성공() throws Exception {
        // Given
        doNothing().when(userService).delete(anyLong());

        // When & Then
        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    @DisplayName("닉네임_중복_확인_존재함")
    void 닉네임_중복_확인_존재함() throws Exception {
        // Given
        DuplicateNicknameRequest request = new DuplicateNicknameRequest("existingNickname");

        when(userService.existNickname(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/users/exist/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).existNickname("existingNickname");
    }

    @Test
    @DisplayName("닉네임_중복_확인_존재하지_않음")
    void 닉네임_중복_확인_존재하지_않음() throws Exception {
        // Given
        DuplicateNicknameRequest request = new DuplicateNicknameRequest("newNickname");

        when(userService.existNickname(anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/v1/users/exist/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));

        verify(userService).existNickname("newNickname");
    }

    @Test
    @DisplayName("이메일_중복_확인_존재함")
    void 이메일_중복_확인_존재함() throws Exception {
        // Given
        DuplicateEmailRequest request = new DuplicateEmailRequest("existing@test.com");

        when(userService.existEmail(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/users/exist/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).existEmail("existing@test.com");
    }

    @Test
    @DisplayName("이메일_중복_확인_존재하지_않음")
    void 이메일_중복_확인_존재하지_않음() throws Exception {
        // Given
        DuplicateEmailRequest request = new DuplicateEmailRequest("new@test.com");

        when(userService.existEmail(anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/v1/users/exist/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));

        verify(userService).existEmail("new@test.com");
    }
}
