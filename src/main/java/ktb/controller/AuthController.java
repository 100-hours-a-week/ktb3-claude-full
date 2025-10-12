package ktb.controller;

import jakarta.validation.Valid;

import ktb.constant.MessageConstant.Success;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ktb.service.AuthService;
import ktb.dto.UserAccountDto;
import ktb.dto.request.LoginRequest;
import ktb.dto.response.CommonResponse;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Void>> login(@Valid @RequestBody LoginRequest request) {
        // session, jwt 사용 시 필요하여 User 정보 반환 받음
        UserAccountDto user = authService.authenticate(request.email(), request.password());

        CommonResponse<Void> response = CommonResponse.of(Success.LOGIN);

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/articles")
                .body(response);
    }
}
