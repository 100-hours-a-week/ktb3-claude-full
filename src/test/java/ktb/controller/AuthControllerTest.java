package ktb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import ktb.auth.adapter.SecurityUserAccount;
import ktb.auth.filter.JwtAuthenticationFilter;
import ktb.config.TestSecurityConfig;
import ktb.domain.UserAccount;
import ktb.dto.request.LoginRequest;
import ktb.fixture.UserAccountFixture;
import ktb.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("로그인_성공")
    void 로그인_성공() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("test@test.com", "Password123!");
        UserAccount user = UserAccountFixture.create(1L, "test@test.com", "testUser");
        SecurityUserAccount securityUser = new SecurityUserAccount(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities()
        );
        String token = "jwt-token-12345";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtTokenProvider.generateToken(anyLong())).thenReturn(token);
        doNothing().when(jwtTokenProvider).addTokenCookie(any(HttpServletResponse.class), anyString());

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isSeeOther())
                .andExpect(header().string("Location", "/articles"))
                .andExpect(jsonPath("$.message").exists());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(1L);
        verify(jwtTokenProvider).addTokenCookie(any(HttpServletResponse.class), anyString());
    }

    @Test
    @DisplayName("로그인_실패_잘못된_이메일")
    void 로그인_실패_잘못된_이메일() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("wrong@test.com", "Password123!");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("로그인_실패_잘못된_비밀번호")
    void 로그인_실패_잘못된_비밀번호() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("test@test.com", "WrongPassword123!");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("로그인_실패_이메일_형식_오류")
    void 로그인_실패_이메일_형식_오류() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("invalid-email", "Password123!");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인_실패_비밀번호_형식_오류")
    void 로그인_실패_비밀번호_형식_오류() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("test@test.com", "weak");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그아웃_성공")
    void 로그아웃_성공() throws Exception {
        // Given
        doNothing().when(jwtTokenProvider).expireTokenCookie(any(HttpServletResponse.class));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(jwtTokenProvider).expireTokenCookie(any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("로그인_후_JWT_토큰_생성_확인")
    void 로그인_후_JWT_토큰_생성_확인() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("test@test.com", "Password123!");
        UserAccount user = UserAccountFixture.create(1L, "test@test.com", "testUser");
        SecurityUserAccount securityUser = new SecurityUserAccount(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities()
        );
        String token = "generated-jwt-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtTokenProvider.generateToken(1L)).thenReturn(token);
        doNothing().when(jwtTokenProvider).addTokenCookie(any(HttpServletResponse.class), anyString());

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isSeeOther());

        verify(jwtTokenProvider).generateToken(1L);
        verify(jwtTokenProvider).addTokenCookie(any(HttpServletResponse.class), anyString());
    }

    @Test
    @DisplayName("로그인_인증_처리_확인")
    void 로그인_인증_처리_확인() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("test@test.com", "Password123!");
        UserAccount user = UserAccountFixture.create(1L, "test@test.com", "testUser");
        SecurityUserAccount securityUser = new SecurityUserAccount(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtTokenProvider.generateToken(anyLong())).thenReturn("token");
        doNothing().when(jwtTokenProvider).addTokenCookie(any(HttpServletResponse.class), anyString());

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isSeeOther());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
