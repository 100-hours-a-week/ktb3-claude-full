package ktb.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import ktb.domain.UserAccount;
import ktb.exception.user.NonExistUserException;
import ktb.repository.UserRepository;
import ktb.util.JwtKeyProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtKeyProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Cookie에서 JWT 추출
            String token = jwtProvider.extractTokenFromRequest(request).orElse(null);

            // 2. JWT 검증 및 userId 추출
            if (token != null && jwtProvider.validateToken(token)) {
                Long userId = jwtProvider.validateAndGetUserId(token);

                // 3. DB 에서 사용자 조회
                UserAccount user = userRepository.findById(userId).orElseThrow(NonExistUserException::new);

                // 4. Authentication 객체 생성 (권한 포함)
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,  // principal: UserAccount 객체
                    null,  // credentials: 비밀번호는 null (이미 인증됨)
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))  // authorities
                );

                // 5. SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("JWT authentication successful for user: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
            // 인증 실패 시에도 필터 체인은 계속 진행 (다른 인증 방식 시도 가능)
        }

        filterChain.doFilter(request, response);
    }
}
