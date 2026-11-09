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

        User.Builder builder = User.builder()
                .id(existing.getId())
                .internalId(existing.getInternalId())
                .username(existing.getUsername())
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
        builder.givenName(command.givenName() != null ? command.givenName() : existing.getGivenName())
               .familyName(command.familyName() != null ? command.familyName() : existing.getFamilyName())
               .middleName(command.middleName() != null ? command.middleName() : existing.getMiddleName())
               .nickname(command.nickname() != null ? command.nickname() : existing.getNickname())
               .gender(command.gender() != null ? command.gender() : existing.getGender())
               .birthdate(command.birthdate() != null ? command.birthdate() : existing.getBirthdate())
               .profileUrl(command.profileUrl() != null ? command.profileUrl() : existing.getProfileUrl())
               .pictureUrl(command.pictureUrl() != null ? command.pictureUrl() : existing.getPictureUrl())
               .websiteUrl(command.websiteUrl() != null ? command.websiteUrl() : existing.getWebsiteUrl())
               .zoneinfo(command.zoneinfo() != null ? command.zoneinfo() : existing.getZoneinfo())
               .locale(command.locale() != null ? command.locale() : existing.getLocale())
               .phoneNumber(command.phoneNumber() != null ? command.phoneNumber() : existing.getPhoneNumber())
               .phoneNumberVerified(existing.getPhoneNumberVerified())
               .addressStreetAddress(command.streetAddress() != null ? command.streetAddress() : existing.getAddressStreetAddress())
               .addressLocality(command.locality() != null ? command.locality() : existing.getAddressLocality())
               .addressRegion(command.region() != null ? command.region() : existing.getAddressRegion())
               .addressPostalCode(command.postalCode() != null ? command.postalCode() : existing.getAddressPostalCode())
               .addressCountry(command.country() != null ? command.country() : existing.getAddressCountry());

        User updatedUser = userRepository.save(builder.build());
        return UserResultMapper.toResult(updatedUser);
    }
}
