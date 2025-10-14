package ktb.exception;

import jakarta.servlet.http.HttpServletRequest;
import ktb.dto.response.CommonResponse;
import ktb.exception.user.NonExistUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handlerMethodArgumentNotValid(MethodArgumentNotValidException me) {
        log.warn("Validation Failed: {}", me.getMessage());

        return ResponseEntity.badRequest()
                .body(CommonResponse.of(me.getMessage()));
    }

    @ExceptionHandler(AuthenticateException.class)
    public ResponseEntity<CommonResponse<Void>> handlerAuthenticationException(
            AuthenticateException ae
            , HttpServletRequest request
    ) {
        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();

        log.warn("Authentication Error | IP: {}, URI: {}",
                clientIp, requestUri);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.of(ae.getMessage()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<CommonResponse<Void>> handlerAuthorizationException(
            AuthorizationException ae
            , HttpServletRequest request
    ) {
        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();

        log.warn("Authorization Error | IP: {}, URI: {}",
                clientIp, requestUri);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.of(ae.getMessage()));
    }

    @ExceptionHandler(ConflictDuplicationException.class)
    public ResponseEntity<CommonResponse<Void>> handlerConflictException(ConflictDuplicationException ce) {
        log.warn("[{}] Duplicate : {}", HttpStatus.CONFLICT, ce.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponse.of(ce.getMessage()));
    }

    @ExceptionHandler(NonExistUserException.class)
    public ResponseEntity<CommonResponse<Void>> handlerNonExistUserException(NonExistUserException nue) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.of(nue.getMessage()));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }
}
