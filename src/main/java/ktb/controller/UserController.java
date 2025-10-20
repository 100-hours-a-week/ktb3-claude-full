package ktb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import ktb.annotation.Authorized;
import ktb.constant.MessageConstant.Success;
import ktb.dto.UserAccountDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
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
                .header(HttpHeaders.LOCATION, "/login")
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
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<UserAccountDto>> search(
            @Parameter(name = "id", description = "User ID(Sequential ID)", required = true)
            @Valid @PathVariable Long id
    ) {
        UserAccountDto user = userService.search(id);

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
    @Authorized
    @PatchMapping("/{id}/nickName")
    public ResponseEntity<Void> patch(
            @Parameter(name = "id", description = "User ID(Sequential ID)", required = true)
            @PathVariable Long id,
            @Valid @RequestBody NickNameUpdateRequest request
    ) {
        userService.updateNickName(id, request.nickName());

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
    @Authorized
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> patch(
            @Parameter(name = "id", description = "User ID(Sequential ID)", required = true)
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        userService.updatePassword(id, request);

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
    @Authorized
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(name = "id", description = "User ID(Sequential ID)", required = true)
            @PathVariable Long id
    ) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
