package ktb.controller;

import jakarta.validation.Valid;

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

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Long>> signUp(@Valid @RequestBody SignupRequest request) {
        Long userId = userService.signUp(request.from());

        CommonResponse<Long> response = CommonResponse.of(Success.SIGNUP, userId);

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .header(HttpHeaders.LOCATION, "/login")
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<UserAccountDto>> search(@Valid @PathVariable Long id) {
        UserAccountDto user = userService.search(id);

        CommonResponse<UserAccountDto> response = CommonResponse.of(Success.RETRIEVAL_USER, user);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/nickName")
    public ResponseEntity<Void> patch(
            @PathVariable Long id,
            @Valid @RequestBody NickNameUpdateRequest request
    ) {
        userService.updateNickName(id, request.nickName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> patch(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        userService.updatePassword(id, request.password());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
