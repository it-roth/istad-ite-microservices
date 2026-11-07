package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecordLoginAttemptUseCase {

    private final UserRepository userRepository;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    public void execute(String usernameOrEmail, boolean success) {
        log.info("Recording login attempt for: {}, success: {}", usernameOrEmail, success);
        userRepository.findByUsernameOrEmail(usernameOrEmail).ifPresent(user -> {
            LocalDateTime now = LocalDateTime.now();
            User.Builder builder = User.builder()
                    .id(user.getId())
                    .internalId(user.getInternalId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .emailVerified(user.getEmailVerified())
                    .passwordHash(user.getPasswordHash())
                    .givenName(user.getGivenName())
                    .familyName(user.getFamilyName())
                    .middleName(user.getMiddleName())
                    .nickname(user.getNickname())
                    .gender(user.getGender())
                    .birthdate(user.getBirthdate())
                    .profileUrl(user.getProfileUrl())
                    .pictureUrl(user.getPictureUrl())
                    .websiteUrl(user.getWebsiteUrl())
                    .zoneinfo(user.getZoneinfo())
                    .locale(user.getLocale())
                    .phoneNumber(user.getPhoneNumber())
                    .phoneNumberVerified(user.getPhoneNumberVerified())
                    .addressStreetAddress(user.getAddressStreetAddress())
                    .addressLocality(user.getAddressLocality())
                    .addressRegion(user.getAddressRegion())
                    .addressPostalCode(user.getAddressPostalCode())
                    .addressCountry(user.getAddressCountry())
                    .enabled(user.getEnabled())
                    .accountNonExpired(user.getAccountNonExpired())
                    .credentialsNonExpired(user.getCredentialsNonExpired())
                    .isDeleted(user.getDeleted())
                    .createdAt(user.getCreatedAt())
                    .createdBy(user.getCreatedBy())
                    .updatedAt(now)
                    .updatedBy("SYSTEM")
                    .version(user.getVersion());

            if (success) {
                builder.failedLoginAttempts(0)
                        .accountNonLocked(true)
                        .lastLoginAt(now);
            } else {
                int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1;
                builder.failedLoginAttempts(attempts)
                        .accountNonLocked(attempts < MAX_FAILED_ATTEMPTS);
            }

            userRepository.save(builder.build());
        });
    }
}

