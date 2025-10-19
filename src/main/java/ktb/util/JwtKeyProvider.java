package ktb.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import ktb.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtKeyProvider {

    private final JwtConfig cfg;
    private final ResourceLoader loader;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    void init() {
        try {
            this.privateKey = readPrivate(cfg.getPrivateKeyPath());
            this.publicKey  = readPublic(cfg.getPublicKeyPath());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA keys", e);
        }
    }

    private PrivateKey readPrivate(String location) throws Exception {
        String pem = readPem(location, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        byte[] der = Base64.getDecoder().decode(pem);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    private PublicKey readPublic(String location) throws Exception {
        String pem = readPem(location, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
        byte[] der = Base64.getDecoder().decode(pem);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
    }

    private String readPem(String loc, String begin, String end) throws IOException {
        Resource r = loader.getResource(loc);
        String s = new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        return s.replace(begin, "")
                .replace(end, "")
                .replaceAll("\\s", "");
    }


    // ✅ JWT 생성 (userId 기반)
    public String generateToken(Long userId) {
        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(now.getTime() + cfg.getAccessExpireMillis());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    // ✅ JWT 검증
    public Long validateAndGetUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

    // ✅ HTTP Request JWT 문자열 추출
    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> "jwt".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    // ✅ userId 추출
    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return null;
        }

        try {
            return validateAndGetUserId(token);
        } catch (JwtException e) {
            return null; // 만료 or 위조된 토큰
        }
    }
}
