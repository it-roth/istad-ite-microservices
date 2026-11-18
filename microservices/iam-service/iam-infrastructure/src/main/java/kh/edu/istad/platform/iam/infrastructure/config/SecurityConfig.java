package kh.edu.istad.platform.iam.infrastructure.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import kh.edu.istad.platform.iam.infrastructure.adapter.CustomOAuth2UserService;
import kh.edu.istad.platform.iam.infrastructure.adapter.CustomOidcUserService;
import kh.edu.istad.platform.iam.infrastructure.adapter.IamOAuth2User;
import kh.edu.istad.platform.iam.infrastructure.adapter.IamUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.oauth2.authorizationserver.issuer}")
    private String issuer;

    private final JdbcTemplate jdbcTemplate;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final IamUserDetailsService iamUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(
                "istad-iam-remember-me-key-2026", iamUserDetailsService);
        rememberMeServices.setParameter("remember-me");
        rememberMeServices.setTokenValiditySeconds(86400 * 30); // 30 days persistent cookie
        rememberMeServices.setAlwaysRemember(false);
        return rememberMeServices;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) -> authorizationServer
                        .oidc(Customizer.withDefaults()) // Enable OpenID Connect 1.0
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated())
                // Redirect to the login page when not authenticated from the
                // authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/login",
                                "/login/2fa",
                                "/login/2fa/**",
                                "/login.html",
                                "/register",
                                "/login/telegram",
                                "/login/telegram/callback",
                                "/oauth2/**",
                                "/.well-known/**",
                                "/login/oauth2/**",
                                "/",
                                "/assets/**",
                                "/favicon.svg",
                                "/icons.svg",
                                "/css/**",
                                "/js/**",
                                "/images/**")
                        .permitAll()
                        .anyRequest().authenticated())
                // Form login handles the redirect to the login page from the
                // authorization server filter chain
                .oauth2Login(
                        config -> config
                                .loginPage("/login")
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)
                                        .oidcUserService(customOidcUserService))
                                .successHandler((request, response, authentication) -> {
                                    try {
                                        jakarta.servlet.http.HttpSession session = request.getSession(true);
                                        if (session != null) {
                                            if (authentication.getPrincipal() instanceof IamOAuth2User oauthUser) {
                                                session.setAttribute("userUuid", oauthUser.getUserUuid().toString());
                                            }
                                            session.setAttribute("browser", "Chrome");
                                            session.setAttribute("device", "Desktop");
                                            session.setAttribute("location", "Phnom Penh, Cambodia");
                                            if (session.getAttribute("iam.link-success") != null) {
                                                String linkedProv = String.valueOf(session.getAttribute("iam.link-success"));
                                                session.removeAttribute("iam.link-success");
                                                response.sendRedirect(
                                                        "/account/linked-accounts?linked=" + linkedProv.toLowerCase());
                                                return;
                                            }
                                        }
                                        response.sendRedirect("/account");
                                    } catch (Exception ex) {
                                        log.error("Error in oauth2 success handler: {}", ex.getMessage(), ex);
                                        response.sendRedirect("/account");
                                    }
                                })
                                .failureHandler((request, response, exception) -> {
                                    log.error("OAuth2 login failed: {}", exception.getMessage(), exception);
                                    jakarta.servlet.http.HttpSession session = request.getSession(false);
                                    if (session != null && session.getAttribute("iam.link-user") != null) {
                                        session.removeAttribute("iam.link-user");
                                        response.sendRedirect("/account/linked-accounts?error=link_failed");
                                        return;
                                    }
                                    response.sendRedirect("/login?error");
                                })
                                .permitAll())
                .oauth2Client(Customizer.withDefaults())
                .rememberMe(remember -> remember
                        .rememberMeServices(rememberMeServices()))
                .formLogin(config -> config
                        .loginPage("/login")
                        .failureHandler((request, response, exception) -> {
                            try {
                                rememberMeServices().loginFail(request, response);
                            } catch (Exception ignored) {}
                            jakarta.servlet.http.HttpSession session = request.getSession(false);
                            if (session != null) {
                                try {
                                    session.invalidate();
                                } catch (Exception ignored) {}
                            }
                            org.springframework.security.core.context.SecurityContextHolder.clearContext();
                            jakarta.servlet.http.Cookie rmCookie = new jakarta.servlet.http.Cookie("remember-me", "");
                            rmCookie.setPath("/");
                            rmCookie.setMaxAge(0);
                            response.addCookie(rmCookie);
                            jakarta.servlet.http.Cookie jsessionCookie = new jakarta.servlet.http.Cookie("JSESSIONID", "");
                            jsessionCookie.setPath("/");
                            jsessionCookie.setMaxAge(0);
                            response.addCookie(jsessionCookie);
                            response.sendRedirect("/login?error");
                        })
                        .successHandler((request, response, authentication) -> {
                            rememberMeServices().loginSuccess(request, response, authentication);
                            String username = authentication.getName();
                            boolean twoFactorRequired = false;
                            String email = null;
                            try {
                                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS two_factor_enabled BOOLEAN NOT NULL DEFAULT FALSE");
                                java.util.List<java.util.Map<String, Object>> rows = jdbcTemplate.queryForList(
                                        "SELECT id, uuid, email, email_verified, two_factor_enabled FROM users WHERE username = ? OR email = ? LIMIT 1",
                                        username, username);
                                if (!rows.isEmpty()) {
                                    java.util.Map<String, Object> row = rows.get(0);
                                    email = (String) row.get("email");
                                    Boolean twoFactorEnabled = (Boolean) row.get("two_factor_enabled");

                                    boolean systemMfaRequired = false;
                                    try {
                                        java.util.List<java.util.Map<String, Object>> secRows = jdbcTemplate.queryForList(
                                                "SELECT mfa_required FROM iam_security_configuration WHERE id = 1");
                                        if (!secRows.isEmpty()) {
                                            systemMfaRequired = Boolean.TRUE.equals(secRows.get(0).get("mfa_required"));
                                        }
                                    } catch (Exception ignored) {}

                                    twoFactorRequired = Boolean.TRUE.equals(twoFactorEnabled) || systemMfaRequired;

                                    UUID userUuid = (UUID) row.get("uuid");
                                    String clientIp = request.getHeader("X-Forwarded-For");
                                    if (clientIp == null || clientIp.isBlank()) {
                                        clientIp = request.getRemoteAddr();
                                    }
                                    if ("0:0:0:0:0:0:0:1".equals(clientIp) || "::1".equals(clientIp)) {
                                        clientIp = "127.0.0.1";
                                    }
                                    String userAgent = request.getHeader("User-Agent");

                                    // Update last_login_at in users table
                                    jdbcTemplate.update(
                                            "UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE uuid = ?",
                                            userUuid);

                                    // Record real login attempt in iam_login_history
                                    jdbcTemplate.update(
                                            "INSERT INTO iam_login_history(user_uuid, principal_name, success, method, user_agent, ip_address, location) VALUES (?, ?, true, 'Password', ?, ?, 'Phnom Penh, Cambodia')",
                                            userUuid, username, userAgent, clientIp);

                                    var session = request.getSession(false);
                                    if (session != null) {
                                        String browser = "Chrome";
                                        String device = "Desktop";
                                        if (userAgent != null) {
                                            String ua = userAgent.toLowerCase();
                                            if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
                                                device = "Mobile";
                                            } else if (ua.contains("tablet") || ua.contains("ipad")) {
                                                device = "Tablet";
                                            }
                                            if (ua.contains("edg")) {
                                                browser = "Edge";
                                            } else if (ua.contains("chrome") && !ua.contains("edg")) {
                                                browser = "Chrome";
                                            } else if (ua.contains("firefox")) {
                                                browser = "Firefox";
                                            } else if (ua.contains("safari") && !ua.contains("chrome")) {
                                                browser = "Safari";
                                            }
                                        }

                                        session.setAttribute("ipAddress", clientIp);
                                        session.setAttribute("userAgent", userAgent);
                                        session.setAttribute("userUuid", userUuid.toString());
                                        session.setAttribute("browser", browser);
                                        session.setAttribute("device", device);
                                        session.setAttribute("location", "Phnom Penh, Cambodia");
                                    }
                                }
                            } catch (Exception ex) {
                                twoFactorRequired = false;
                            }

                            // Retrieve requested URL before authentication redirect
                            org.springframework.security.web.savedrequest.HttpSessionRequestCache requestCache = new org.springframework.security.web.savedrequest.HttpSessionRequestCache();
                            org.springframework.security.web.savedrequest.SavedRequest savedRequest = requestCache
                                    .getRequest(request, response);
                            String targetUrl = "/account";
                            if (savedRequest != null && savedRequest.getRedirectUrl() != null) {
                                String savedUrl = savedRequest.getRedirectUrl();
                                String lower = savedUrl.toLowerCase();
                                if (!lower.contains("/login") && !lower.contains("/assets/") && !lower.contains("/api/")
                                        && !lower.contains(".well-known") && !lower.contains("devtools")
                                        && !lower.endsWith(".json") && !lower.endsWith(".ico") && !lower.endsWith(".svg")
                                        && (lower.contains("/account") || lower.contains("/admin") || lower.contains("/oauth2/authorize"))) {
                                    targetUrl = savedUrl;
                                }
                            }

                            if (twoFactorRequired) {
                                String redirectUrl = "/login/2fa?username="
                                        + java.net.URLEncoder.encode(username, java.nio.charset.StandardCharsets.UTF_8);
                                if (email != null && !email.isBlank()) {
                                    redirectUrl += "&email=" + java.net.URLEncoder.encode(email,
                                            java.nio.charset.StandardCharsets.UTF_8);
                                }
                                if (!"/account".equals(targetUrl)) {
                                    redirectUrl += "&target=" + java.net.URLEncoder.encode(targetUrl,
                                            java.nio.charset.StandardCharsets.UTF_8);
                                }
                                response.sendRedirect(redirectUrl);
                            } else {
                                response.sendRedirect(targetUrl);
                            }
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll());

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository());
    }

    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository());
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }

}
