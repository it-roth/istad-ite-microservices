package kh.edu.istad.platform.iam.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kh.edu.istad.platform.iam.domain.dto.AuthUserResult;
import kh.edu.istad.platform.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.domain.usecase.FindOrProvisionFederatedUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.LinkFederatedIdentityUseCase;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebTelegramController {

    private final FindOrProvisionFederatedUserUseCase findOrProvisionFederatedUserUseCase;
    private final LinkFederatedIdentityUseCase linkFederatedIdentityUseCase;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Value("${TELEGRAM_BOT_TOKEN}")
    private String botToken;

    @Value("${TELEGRAM_BOT_USERNAME}")
    private String botUsername;

    @Value("${telegram.public-base-url}")
    private String telegramPublicBaseUrl;

    @GetMapping("/login/telegram")
    public String telegramLoginPage(Model model, HttpServletRequest request) {
        URI publicLoginUri = URI.create(telegramPublicBaseUrl).resolve("/login/telegram");
        if (!publicLoginUri.getHost().equalsIgnoreCase(request.getServerName())) {
            return "redirect:" + publicLoginUri;
        }

        model.addAttribute("botUsername", botUsername);
        return "telegram-login";
    }

    @GetMapping("/login/telegram/callback")
    public String handleTelegramCallback(
            @RequestParam Map<String, String> allParams,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.info("Received Telegram callback with parameters: {}", allParams.keySet());

        String receivedHash = allParams.get("hash");
        if (receivedHash == null || receivedHash.isBlank()) {
            throw new IllegalArgumentException("Missing Telegram hash in callback parameters");
        }
        String authDateValue = allParams.get("auth_date");
        if (authDateValue == null || authDateValue.isBlank()) {
            throw new IllegalArgumentException("Missing Telegram authentication date");
        }

        Instant authDate;
        try {
            authDate = Instant.ofEpochSecond(Long.parseLong(authDateValue));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid Telegram authentication date", ex);
        }
        Instant now = Instant.now();
        if (authDate.isAfter(now.plusSeconds(30)) || authDate.isBefore(now.minus(Duration.ofMinutes(5)))) {
            throw new IllegalArgumentException("Telegram authentication data has expired");
        }

        Map<String, String> dataParams = new HashMap<>(allParams);
        dataParams.remove("hash");

        String computedHash = calculateTelegramHash(dataParams, botToken);
        if (!MessageDigest.isEqual(
                computedHash.getBytes(StandardCharsets.US_ASCII),
                receivedHash.trim().toLowerCase().getBytes(StandardCharsets.US_ASCII)
        )) {
            log.warn("Telegram signature verification failed");
            throw new IllegalArgumentException("Invalid Telegram signature hash");
        }

        String subject = allParams.get("id");
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Missing Telegram user identifier");
        }
        String firstName = allParams.get("first_name");
        String lastName = allParams.get("last_name");
        String username = allParams.get("username");
        String photoUrl = allParams.get("photo_url");

        String displayName = firstName;
        if (firstName != null && lastName != null) {
            displayName = firstName + " " + lastName;
        } else if (lastName != null) {
            displayName = lastName;
        }

        Map<String, Object> attributes = new HashMap<>(allParams);

        FederatedIdentityCommand command = new FederatedIdentityCommand(
                SocialProvider.TELEGRAM,
                subject,
                null,
                false,
                displayName,
                username,
                photoUrl,
                attributes
        );

        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUserResult authUser;
        if (currentAuthentication != null
                && currentAuthentication.isAuthenticated()
                && !(currentAuthentication instanceof AnonymousAuthenticationToken)) {
            authUser = linkFederatedIdentityUseCase.execute(currentAuthentication.getName(), command);
        } else {
            authUser = findOrProvisionFederatedUserUseCase.execute(command);
        }

        List<SimpleGrantedAuthority> authorities = authUser.authorities() != null
                ? authUser.authorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                authUser.username(),
                null,
                authorities
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        log.info("Telegram authentication successful for user: {} (UUID: {})", authUser.username(), authUser.uuid());
        return "redirect:/";
    }

    private static String calculateTelegramHash(Map<String, String> dataParams, String token) {
        try {
            TreeMap<String, String> sortedMap = new TreeMap<>(dataParams);
            StringBuilder dataCheckString = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isBlank()) {
                    if (dataCheckString.length() > 0) {
                        dataCheckString.append("\n");
                    }
                    dataCheckString.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = md.digest(token.getBytes(StandardCharsets.UTF_8));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(dataCheckString.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : rawHmac) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to calculate Telegram hash", ex);
        }
    }
}
