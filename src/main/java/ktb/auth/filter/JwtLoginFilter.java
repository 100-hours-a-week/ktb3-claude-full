package ktb.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import ktb.constant.MessageConstant;
import ktb.domain.UserAccount;
import ktb.dto.request.LoginRequest;
import ktb.dto.response.CommonResponse;
import ktb.exception.AuthenticateException;
import ktb.repository.UserRepository;
import ktb.util.JwtKeyProvider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static ktb.auth.util.Mapper.deserializationRequestToLoginRequest;

@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final UserRepository userRepository;
    private final JwtKeyProvider jwtProvider;
    private final ObjectMapper objectMapper;

    public JwtLoginFilter(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtKeyProvider jwtKeyProvider
    ) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/v1/auth/login");
        this.userRepository = userRepository;
        this.jwtProvider = jwtKeyProvider;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        LoginRequest requestBody = deserializationRequestToLoginRequest(request);

        log.debug("Login attempt for email: {}", requestBody.email());

        Long userId = userRepository.findByEmail(requestBody.email())
                .orElseThrow(AuthenticateException::new)
                .getId();

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(userId, requestBody.password());

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) throws IOException, ServletException {
        // Authentication의 principal에서 email 추출
        Long id = Long.parseLong(authentication.getName());

        UserAccount user = userRepository.findById(id)
                        .orElseThrow(AuthenticateException::new);

        // JWT Token 발급
        String token = jwtProvider.generateToken(user.getId());

        // JWT Cookie 설정
        jwtProvider.addTokenCookie(response, token);

        // JSON 응답
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader(HttpHeaders.LOCATION, "/articles");

        CommonResponse<Void> responseBody = CommonResponse.of(MessageConstant.Success.LOGIN);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));

        log.info("Login successful for user: {} (userId: {})", user.getEmail(), user.getId());

        super.successfulAuthentication(request, response, chain, authentication);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {
        log.warn("Login failed: {}", failed.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        CommonResponse<Void> responseBody = CommonResponse.of(MessageConstant.User.LOGIN_FAILED);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));

        super.unsuccessfulAuthentication(request, response, failed);
    }
}
