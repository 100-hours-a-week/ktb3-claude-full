package ktb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import ktb.config.JwtConfig;
import ktb.constant.MessageConstant.Success;
import ktb.util.JwtKeyProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ktb.service.AuthService;
import ktb.dto.UserAccountDto;
import ktb.dto.request.LoginRequest;
import ktb.dto.response.CommonResponse;

@Tag(name = "Authentication", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtKeyProvider jwt;
    private final JwtConfig cfg;
    private final AuthService authService;

    @Operation(
            summary = "Login",
            description = "로그인 시도 (ID(Email)/PW)",
            tags = { "Authentication" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = LoginRequest.class))),
            }
    )
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Void>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse httpResponse
    ) {
        // session, jwt 사용 시 필요하여 User 정보 반환 받음
        UserAccountDto user = authService.authenticate(request.email(), request.password());

        // ✅ 2. JWT 발급 (userId + nickName)
        Long userId = user.id();
        String nickName = user.nickName();
        String token = jwt.generateToken(userId, nickName);

        // ✅ 3. 쿠키 설정
        Cookie cookie = new Cookie(cfg.getAccessTokenName(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)cfg.getAccessExpireSeconds());
        httpResponse.addCookie(cookie);

        CommonResponse<Void> response = CommonResponse.of(Success.LOGIN);

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/articles")
                .body(response);
    }

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
        Cookie cookie = new Cookie(cfg.getAccessTokenName(), null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpResponse.addCookie(cookie);

        return ResponseEntity
                .ok(CommonResponse.of(Success.LOGOUT));
    }
}
