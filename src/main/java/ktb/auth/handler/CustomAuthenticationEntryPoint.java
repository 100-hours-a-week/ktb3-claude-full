package ktb.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ktb.constant.MessageConstant;
import ktb.dto.response.CommonResponse;
import ktb.util.RequestParsing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 시 처리를 담당하는 Custom AuthenticationEntryPoint
 * BadCredentialsException 등 인증 예외 발생 시 401 Unauthorized 응답을 반환합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        String clientIp = RequestParsing.getClientIp(request);
        String requestUri = request.getRequestURI();

        log.warn("Authentication Failed | IP: {}, URI: {}, Reason: {}",
                clientIp, requestUri, authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CommonResponse<Void> errorResponse = CommonResponse.of(
                MessageConstant.User.LOGIN_FAILED
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
