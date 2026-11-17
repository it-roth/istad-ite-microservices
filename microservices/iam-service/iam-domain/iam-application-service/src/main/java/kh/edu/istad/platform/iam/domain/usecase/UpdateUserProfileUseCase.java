package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.UserResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateUserProfileUseCase {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;

    public UserResult execute(UpdateUserProfileCommand command) {
        log.info("Updating user profile for UUID: {}", command.userUuid());
        User existing = userRepository.findByUuid(command.userUuid())
                .orElseThrow(() -> new IamDomainException("User not found with uuid: " + command.userUuid()));

        String actor = currentActorProvider.getCurrentActor();
        LocalDateTime now = LocalDateTime.now();
        String email = existing.getEmail();
        Boolean emailVerified = existing.getEmailVerified();

        if (command.email() != null) {
            email = command.email().trim().toLowerCase(Locale.ROOT);
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new IamDomainException("Email address is invalid");
            }
            if (!email.equalsIgnoreCase(existing.getEmail())) {
                if (userRepository.existsByEmail(email)) {
                    throw new IamDomainException("Email address is already registered");
                }
                emailVerified = false;
            }
        }

        String username = existing.getUsername();
        if (command.username() != null && !command.username().isBlank()) {
            String newUsername = command.username().trim();
            if (!newUsername.equalsIgnoreCase(existing.getUsername())) {
                if (userRepository.existsByUsername(newUsername)) {
                    throw new IamDomainException("Username is already taken");
                }
                username = newUsername;
            }
        }

        User.Builder builder = User.builder()
                .id(existing.getId())
                .internalId(existing.getInternalId())
                .username(username)
                .email(email)
                .emailVerified(emailVerified)
                .passwordHash(existing.getPasswordHash())
                .enabled(existing.getEnabled())
                .accountNonExpired(existing.getAccountNonExpired())
                .accountNonLocked(existing.getAccountNonLocked())
                .credentialsNonExpired(existing.getCredentialsNonExpired())
                .failedLoginAttempts(existing.getFailedLoginAttempts())
                .lastLoginAt(existing.getLastLoginAt())
                .passwordChangedAt(existing.getPasswordChangedAt())
                .isDeleted(existing.getDeleted())
                .deletedAt(existing.getDeletedAt())
                .deletedBy(existing.getDeletedBy())
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
                .updatedAt(now)
                .updatedBy(actor)
                .version(existing.getVersion());

        // Apply profile updates
        String cleanPhone = command.phoneNumber() != null
                ? (command.phoneNumber().isBlank() ? null : command.phoneNumber().trim())
                : existing.getPhoneNumber();
        String cleanGender = command.gender() != null
                ? (command.gender().isBlank() ? null : command.gender().trim())
                : existing.getGender();
        String cleanCountry = command.country() != null
                ? (command.country().isBlank() ? null : command.country().trim().toUpperCase(Locale.ROOT))
                : existing.getAddressCountry();
        if (cleanCountry != null && cleanCountry.length() > 2) {
            cleanCountry = cleanCountry.substring(0, 2);
        }

        String cleanMiddle = command.middleName() != null
                ? (command.middleName().isBlank() ? null : command.middleName().trim())
                : existing.getMiddleName();
        String cleanNickname = command.nickname() != null
                ? (command.nickname().isBlank() ? null : command.nickname().trim())
                : existing.getNickname();
        String cleanWebsite = command.websiteUrl() != null
                ? (command.websiteUrl().isBlank() ? null : command.websiteUrl().trim())
                : existing.getWebsiteUrl();
        String cleanPicture = command.pictureUrl() != null
                ? (command.pictureUrl().isBlank() ? null : command.pictureUrl().trim())
                : existing.getPictureUrl();
        String cleanStreet = command.streetAddress() != null
                ? (command.streetAddress().isBlank() ? null : command.streetAddress().trim())
                : existing.getAddressStreetAddress();
        String cleanLocality = command.locality() != null
                ? (command.locality().isBlank() ? null : command.locality().trim())
                : existing.getAddressLocality();
        String cleanRegion = command.region() != null
                ? (command.region().isBlank() ? null : command.region().trim())
                : existing.getAddressRegion();
        String cleanPostal = command.postalCode() != null
                ? (command.postalCode().isBlank() ? null : command.postalCode().trim())
                : existing.getAddressPostalCode();

        builder.givenName(command.givenName() != null ? command.givenName().trim() : existing.getGivenName())
               .familyName(command.familyName() != null ? command.familyName().trim() : existing.getFamilyName())
               .middleName(cleanMiddle)
               .nickname(cleanNickname)
               .gender(cleanGender)
               .birthdate(command.birthdate() != null ? command.birthdate() : existing.getBirthdate())
               .profileUrl(command.profileUrl() != null ? command.profileUrl() : existing.getProfileUrl())
               .pictureUrl(cleanPicture)
               .websiteUrl(cleanWebsite)
               .zoneinfo(command.zoneinfo() != null ? command.zoneinfo() : existing.getZoneinfo())
               .locale(command.locale() != null ? command.locale() : existing.getLocale())
               .phoneNumber(cleanPhone)
               .phoneNumberVerified(existing.getPhoneNumberVerified())
               .addressStreetAddress(cleanStreet)
               .addressLocality(cleanLocality)
               .addressRegion(cleanRegion)
               .addressPostalCode(cleanPostal)
               .addressCountry(cleanCountry);

        User updatedUser = userRepository.save(builder.build());
        return UserResultMapper.toResult(updatedUser);
    }
}
