package ktb.dto.response;

import jakarta.validation.constraints.NotNull;

import lombok.Builder;

@Builder
public record CommonResponse<T>(
        @NotNull
        String message,
        T data
) {
}
