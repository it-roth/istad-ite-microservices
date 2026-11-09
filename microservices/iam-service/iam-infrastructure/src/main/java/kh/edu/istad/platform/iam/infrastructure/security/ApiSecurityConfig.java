package kh.edu.istad.platform.iam.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    private final ApiProblemDetailHandler apiProblemDetailHandler;

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                            .anyRequest().authenticated();
                })
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(apiProblemDetailHandler)
                        .accessDeniedHandler(apiProblemDetailHandler)
                )
                .oauth2ResourceServer(oauth2 -> {
                    oauth2
                            .authenticationEntryPoint(apiProblemDetailHandler)
                            .accessDeniedHandler(apiProblemDetailHandler)
                            .jwt(jwt -> {
                                jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter);
                            });
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });

        return http.build();
    }
}
