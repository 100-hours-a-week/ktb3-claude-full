package ktb.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import ktb.dto.response.CommonResponse;
import ktb.exception.article.NoExistArticleException;
import ktb.exception.user.NonExistUserException;
import ktb.util.RequestParsing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Tag(name = "Error Response", description = "API 요청 시 발생 가능한 예외와 에러 응답 형식을 정의합니다.")
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponse(responseCode = "400", description = "Request 유효성 검증 실패")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handlerMethodArgumentNotValid(MethodArgumentNotValidException me) {
        log.warn("Validation Failed: {}", me.getMessage());

        return ResponseEntity.badRequest()
                .body(CommonResponse.of(me.getMessage()));
    }

    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ExceptionHandler(AuthenticateException.class)
    public ResponseEntity<CommonResponse<Void>> handlerAuthenticationException(
            AuthenticateException ae
            , HttpServletRequest request
    ) {
        String clientIp = RequestParsing.getClientIp(request);
        String requestUri = request.getRequestURI();

        log.warn("Authentication Error | IP: {}, URI: {}",
                clientIp, requestUri);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.of(ae.getMessage()));
    }

    @ApiResponse(responseCode = "403", description = "권한 불충분")
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<CommonResponse<Void>> handlerAuthorizationException(
            AuthorizationException ae
            , HttpServletRequest request
    ) {
        String clientIp = RequestParsing.getClientIp(request);
        String requestUri = request.getRequestURI();

        log.warn("Authorization Error | IP: {}, URI: {}",
                clientIp, requestUri);

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CommonResponse.of(ae.getMessage()));
    }

    @ApiResponse(responseCode = "409", description = "데이터 중복 발생")
    @ExceptionHandler(ConflictDuplicationException.class)
    public ResponseEntity<CommonResponse<Void>> handlerConflictException(ConflictDuplicationException ce) {
        log.warn("[{}] Duplicate : {}", HttpStatus.CONFLICT, ce.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponse.of(ce.getMessage()));
    }

    @ApiResponse(responseCode = "404", description = "유저 미 존재")
    @ExceptionHandler(NonExistUserException.class)
    public ResponseEntity<CommonResponse<Void>> handlerNonExistUserException(NonExistUserException nue) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.of(nue.getMessage()));
    }

    @ApiResponse(responseCode = "404", description = "게시글 미 존재")
    @ExceptionHandler(NoExistArticleException.class)
    public ResponseEntity<CommonResponse<Void>> handleNoExistArticle(NoExistArticleException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.of(ex.getMessage()));
    }
}
