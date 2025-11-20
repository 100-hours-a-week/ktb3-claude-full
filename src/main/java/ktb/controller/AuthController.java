package ktb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import ktb.auth.adapter.SecurityUserAccount;
import ktb.constant.MessageConstant.Success;
import ktb.dto.request.LoginRequest;
import ktb.dto.response.CommonResponse;
import ktb.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

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
        // 1. 인증
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),      // principal
                        request.password()    // credentials
                )
        );

        // 2. JWT 생성
        SecurityUserAccount userDetails = (SecurityUserAccount) auth.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userDetails.getAccount().getId());

        // 3. 쿠키 설정
        jwtTokenProvider.addTokenCookie(httpResponse, jwt);


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
        jwtTokenProvider.expireTokenCookie(httpResponse);

        return ResponseEntity.ok(CommonResponse.of(Success.LOGOUT));
    }
}
