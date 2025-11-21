package ktb.auth.config;

import java.util.List;

import ktb.auth.filter.CsrfDebugFilter;
import ktb.auth.filter.JwtAuthenticationFilter;
import ktb.auth.service.CustomUserDetailService;

import ktb.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SecurityProperties securityProperties;
    private final CustomUserDetailService customUserDetailService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CsrfDebugFilter csrfDebugFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000", "http://localhost:5050"));
                    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                    cfg.setAllowedHeaders(List.of("Content-Type","X-XSRF-TOKEN","Authorization"));
                    cfg.setAllowCredentials(true); // 쿠키 전송
                    return cfg;
                }))
                .csrf(csrf -> csrf.csrfTokenRepository(csrfRepo()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                );

        // SecurityProperties 패턴 가져와서 설정
        http
                .authorizeHttpRequests(auth -> {
                    // permitAll: 모든 사용자 접근 가능
                    auth.requestMatchers(securityProperties.getPermitAllPatterns())
                            .permitAll();

                    // anonymous: 미인증 사용자만 접근 가능
                    auth.requestMatchers(securityProperties.getAnonymousOnlyPatterns())
                            .anonymous();

                    // 나머지는 인증 필요
                    auth.anyRequest()
                            .authenticated();
                });

        // Filter 등록
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // csrf Debug Logging
        http.addFilterBefore(csrfDebugFilter, CsrfFilter.class);

        return http.build();
    }

    @Bean
    CsrfTokenRepository csrfRepo() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
}
