package ktb.dto.response;

import jakarta.validation.constraints.NotNull;

public record CommonResponse<T>(
        @NotNull
        String message,
        T data
) {
        public static CommonResponse<Void> of(String message) {
                return new CommonResponse<>(message, null);
        }

        public static <T> CommonResponse<T> of(String message, T data) {
                return new CommonResponse<>(message, data);
        }
}
