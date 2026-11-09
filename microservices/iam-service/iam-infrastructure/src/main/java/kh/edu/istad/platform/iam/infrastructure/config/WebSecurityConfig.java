package kh.edu.istad.platform.iam.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.edu.istad.platform.iam.infrastructure.security.social.CustomOAuth2UserService;
import kh.edu.istad.platform.iam.infrastructure.security.social.CustomOidcUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Intercept Telegram OAuth2 link BEFORE any Spring Security OAuth2 filter processes it
                .addFilterBefore(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    FilterChain filterChain)
                            throws ServletException, IOException {
                        String uri = request.getRequestURI();
                        if ("/oauth2/authorization/telegram".equals(uri)) {
                            response.sendRedirect("/login/telegram");
                            return;
                        }
                        filterChain.doFilter(request, response);
                    }
                }, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers(
                                    "/login",
                                    "/login/**",
                                    "/register",
                                    "/register/**",
                                    "/oauth2/authorization/**",
                                    "/error",
                                    "/css/**",
                                    "/js/**",
                                    "/images/**",
                                    "/webjars/**",
                                    "/favicon.ico"
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                .formLogin(form -> {
                    form
                            .loginPage("/login")
                            .permitAll();
                })
                .oauth2Login(oauth2 -> {
                    oauth2
                            .loginPage("/login")
                            .userInfoEndpoint(userInfo -> {
                                userInfo
                                        .userService(customOAuth2UserService)
                                        .oidcUserService(customOidcUserService);
                            });
                });

        return http.build();
    }
}

