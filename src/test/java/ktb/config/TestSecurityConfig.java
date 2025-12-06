package ktb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ktb.auth.handler.CustomAuthenticationEntryPoint;
import ktb.auth.service.CustomUserDetailService;
import ktb.util.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return http.build();
    }
}
