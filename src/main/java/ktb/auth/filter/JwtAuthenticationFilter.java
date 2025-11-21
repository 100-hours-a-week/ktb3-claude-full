package ktb.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import ktb.auth.adapter.SecurityUserAccount;
import ktb.auth.service.CustomUserDetailService;
import ktb.config.SecurityProperties;
import ktb.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecurityProperties securityProperties;
    private final JwtTokenProvider jwtProvider;
    private final CustomUserDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtProvider.extractTokenFromRequest(request).orElse(null);

        if (token == null) {
            filterChain.doFilter(request, response);

            return;
        }

        if (!jwtProvider.validateToken(token)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);

            return;
        }

        try {
            authenticateWithJwt(token);
        } catch (AuthenticationException e) {
            log.info("JWT authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateWithJwt(String jwt) {
        Long userId = jwtProvider.getUserIdFromToken(jwt);

        SecurityUserAccount userDetails = (SecurityUserAccount) userDetailsService.loadUserById(userId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // public 경로 (permitAll + anonymous)는 JWT 필터를 거치지 않음
        boolean isPublic = securityProperties.isPublicEndpoint(request);

        if (isPublic && log.isDebugEnabled()) {
            String type = securityProperties.isPermitAllEndpoint(request)
                    ? "permitAll"
                    : "anonymous";
            log.debug("Skipping JWT filter for {} endpoint: {}",
                    type, request.getRequestURI());
        }

        return isPublic;
    }
}
