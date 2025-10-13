package ktb.dto.response;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

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

        public static <T> CommonResponse<Collection<T>> of(String message, Collection<T> data) {
                return new CommonResponse<>(message, List.copyOf(data));
        }
}
