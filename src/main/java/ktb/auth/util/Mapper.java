package ktb.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import ktb.dto.request.LoginRequest;
import ktb.exception.filter.JsonDeserializationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static LoginRequest deserializationRequestToLoginRequest(HttpServletRequest request) {
        try {
            return OBJECT_MAPPER.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException ie) {
            throw new JsonDeserializationException();
        }
    }
}
