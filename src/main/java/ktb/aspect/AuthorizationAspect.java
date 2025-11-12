package ktb.aspect;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

import ktb.annotation.Authorized;
import ktb.util.JwtKeyProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {
    private final JwtKeyProvider jwtProvider;

    @Around("@annotation(authorized)")
    public Object verifyToken(ProceedingJoinPoint joinPoint, Authorized authorized) throws Throwable {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // ✅ 1. 쿠키에서 JWT 추출
        String token = null;
        if (request.getCookies() != null) {
            token = jwtProvider.extractTokenFromRequest(request).orElse(null);
        }

        if (token == null) {
            log.warn("JWT Cookie not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing JWT Token");
        }

        try {
            // ✅ 2. JWT 검증
            Long userId = jwtProvider.validateAndGetUserId(token);
            String userNickName = jwtProvider.getNickNameFromRequest(request);
            request.setAttribute("userId", userId);
            request.setAttribute("userNickname", userNickName);

            // ✅ 3. Controller 실행
            Object result = joinPoint.proceed();

            // ✅ 4. Controller 실행 후 로그 남기기
            log.info("[AOP] userId={} executed {}", userId, joinPoint.getSignature());
            return result;

        } catch (JwtException e) {
            log.error("JWT invalid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or Expired JWT");
        }
    }
}
