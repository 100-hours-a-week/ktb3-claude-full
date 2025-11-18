package ktb.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CsrfDebugFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CsrfDebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String headerToken = request.getHeader("X-XSRF-TOKEN");
        String cookieToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "XSRF-TOKEN".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        log.warn("[CSRF DEBUG] uri={}, method={}, headerToken={}, cookieToken={}",
                request.getRequestURI(), request.getMethod(), headerToken, cookieToken);

        // CsrfFilter가 생성해 둔 _csrf attribute도 확인 가능
        Object csrfAttr = request.getAttribute("_csrf");
        if (csrfAttr instanceof CsrfToken token) {
            log.warn("[CSRF DEBUG] request attr _csrf token={}", token.getToken());
        }

        filterChain.doFilter(request, response);
    }
}
