package ktb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import ktb.constant.MessageConstant.Success;
import ktb.dto.UserAccountDto;
import ktb.dto.request.DuplicateEmailRequest;
import ktb.dto.request.DuplicateNicknameRequest;
import ktb.dto.request.NickNameUpdateRequest;
import ktb.dto.request.PasswordUpdateRequest;
import ktb.dto.request.SignupRequest;
import ktb.dto.response.CommonResponse;
import ktb.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "User signup",
            description = "회원가입 합니다.",
            tags = { "User" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = SignupRequest.class))),
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Long>> signUp(@Valid @RequestBody SignupRequest request) {
        Long userId = userService.signUp(request.from());

        CommonResponse<Long> response = CommonResponse.of(Success.SIGNUP, userId);

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/api/v1/auth/login")
                .body(response);
    }

    @Operation(
            summary = "User search(detail)",
            description = "유저 상세 검색",
            tags = { "User" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = SignupRequest.class))),
            }
    )
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserAccountDto>> search(
            @RequestAttribute Long userId
    ) {
        UserAccountDto user = userService.search(userId);

        CommonResponse<UserAccountDto> response = CommonResponse.of(Success.RETRIEVAL_USER, user);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "User update(nickname)",
            description = "닉네임 변경",
            tags = { "User" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = NickNameUpdateRequest.class))),
            }
    )
    @PatchMapping("/me/nickName")
    public ResponseEntity<Void> patch(
            @RequestAttribute Long userId,
            @Valid @RequestBody NickNameUpdateRequest request
    ) {
        userService.updateNickName(userId, request.nickName());

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "User update(password)",
            description = "패스워드 변경",
            tags = { "User" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = PasswordUpdateRequest.class))),
            }
    )
    @PatchMapping("/me/password")
    public ResponseEntity<Void> patch(
            @RequestAttribute Long userId,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        userService.updatePassword(userId, request);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "User delete",
            description = "유저 삭제",
            tags = { "User" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
            }
    )
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(
            @RequestAttribute Long userId
    ) {
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "닉네임 등록 여부",
            description = "닉네임 중복 유효성 검사 목적",
            tags = { "User" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
            }
    )
    @PostMapping("/exist/nickname")
    public ResponseEntity<CommonResponse<Boolean>> existsNickname(
            @RequestBody DuplicateNicknameRequest request
    ) {
        boolean isExist = userService.existNickname(request.nickname());

        CommonResponse<Boolean> response = CommonResponse.of("", isExist);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "이메일 등록 여부",
            description = "이메일 중복 유효성 검사 목적",
            tags = { "User" },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
            }
    )
    @PostMapping("/exist/email")
    public ResponseEntity<CommonResponse<Boolean>> existsEmail(
            @RequestBody DuplicateEmailRequest request
    ) {
        boolean isExist = userService.existEmail(request.email());

        CommonResponse<Boolean> response = CommonResponse.of("", isExist);

        return ResponseEntity.ok(response);
    }
}
