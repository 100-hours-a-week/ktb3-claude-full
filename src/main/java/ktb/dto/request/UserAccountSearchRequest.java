package ktb.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserAccountSearchRequest (
        @NotBlank
        @Email
        String email
){
}
