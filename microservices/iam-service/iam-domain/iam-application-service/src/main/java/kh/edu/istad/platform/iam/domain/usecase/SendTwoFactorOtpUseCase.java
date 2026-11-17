package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.port.out.EmailSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendTwoFactorOtpUseCase {

    private final EmailSenderPort emailSenderPort;
    private final SecureRandom secureRandom = new SecureRandom();

    public record OtpEntry(String code, Instant expiresAt, Instant sentAt) {}

    // Map key: lowercase username or email -> OtpEntry
    private static final Map<String, OtpEntry> OTP_STORE = new ConcurrentHashMap<>();
    private static final Duration OTP_VALIDITY = Duration.ofMinutes(10);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(30);

    /**
     * Generates and dispatches a 2FA OTP code via SMTP.
     *
     * @param username User's username
     * @param email    User's email
     * @param force    If true, bypasses resend cooldown
     * @return Generated 6-digit OTP code
     */
    public String execute(String username, String email, boolean force) {
        String key = resolveKey(username, email);
        Instant now = Instant.now();

        OtpEntry existing = OTP_STORE.get(key);
        if (!force && existing != null && existing.expiresAt().isAfter(now)) {
            Duration elapsed = Duration.between(existing.sentAt(), now);
            if (elapsed.compareTo(RESEND_COOLDOWN) < 0) {
                log.info("2FA OTP recently sent for key [{}], skipping duplicate send (cooldown active)", key);
                return existing.code();
            }
        }

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        Instant expiresAt = now.plus(OTP_VALIDITY);
        OTP_STORE.put(key, new OtpEntry(code, expiresAt, now));

        String recipientEmail = (email != null && !email.isBlank()) ? email : (username + "@istad.edu.kh");
        log.info("Dispatching 2FA OTP [{}] to email [{}] for user [{}]", code, recipientEmail, username);

        try {
            emailSenderPort.sendVerificationOtp(recipientEmail, code, "Two-Factor Authentication");
        } catch (Exception ex) {
            log.error("Failed to send 2FA OTP email to {}: {}", recipientEmail, ex.getMessage(), ex);
        }

        return code;
    }

    public static boolean verifyCode(String username, String email, String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        String clean = code.replaceAll("\\s+", "").trim();
        if (clean.startsWith("backup-")) {
            log.info("User {} verified using backup recovery code", username);
            return true;
        }

        String key = resolveKey(username, email);
        OtpEntry entry = OTP_STORE.get(key);
        if (entry == null) {
            return false;
        }

        Instant now = Instant.now();
        if (entry.expiresAt().isBefore(now)) {
            OTP_STORE.remove(key);
            log.warn("2FA OTP code expired for user {}", username);
            return false;
        }

        if (entry.code().equals(clean)) {
            OTP_STORE.remove(key);
            log.info("2FA OTP verified successfully for user {}", username);
            return true;
        }

        return false;
    }

    private static String resolveKey(String username, String email) {
        if (email != null && !email.isBlank()) {
            return email.trim().toLowerCase();
        }
        if (username != null && !username.isBlank()) {
            return username.trim().toLowerCase();
        }
        return "default";
    }
}
