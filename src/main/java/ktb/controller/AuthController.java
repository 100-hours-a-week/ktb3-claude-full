package ktb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

import ktb.config.JwtConfig;
import ktb.constant.MessageConstant.Success;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ktb.dto.response.CommonResponse;

@Tag(name = "Authentication", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtConfig cfg;

    // Login: JwtLoginFilter 처리 진행, Controller 제거

    @Operation(
            summary = "Logout",
            description = "로그아웃 처리 (쿠키 만료)",
            tags = { "Authentication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpServletResponse httpResponse) {
        // JWT Cookie 만료 처리
        ResponseCookie cookie = ResponseCookie.from(cfg.getAccessTokenName(), "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)  // 쿠키 즉시 만료
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(CommonResponse.of(Success.LOGOUT));
    }
}
