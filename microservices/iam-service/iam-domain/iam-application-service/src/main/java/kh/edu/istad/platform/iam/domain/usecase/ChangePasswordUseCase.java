package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.ChangePasswordCommand;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PasswordHasher;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;

    public void execute(ChangePasswordCommand command) {
        log.info("Changing password for user UUID: {}", command.userUuid());
        User user = userRepository.findByUuid(command.userUuid())
                .orElseThrow(() -> new IamDomainException("User not found: " + command.userUuid()));

        if (user.getPasswordHash() != null && !passwordHasher.matches(command.oldPassword(), user.getPasswordHash())) {
            throw new IamDomainException("Current password does not match");
        }

        LocalDateTime now = LocalDateTime.now();
        String actor = currentActorProvider.getCurrentActor();

        User updatedUser = User.builder()
                .id(user.getId())
                .internalId(user.getInternalId())
                .username(user.getUsername())
                .email(user.getEmail())
                .emailVerified(user.getEmailVerified())
                .passwordHash(passwordHasher.encode(command.newPassword()))
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
                .accountNonLocked(user.getAccountNonLocked())
                .credentialsNonExpired(true)
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lastLoginAt(user.getLastLoginAt())
                .passwordChangedAt(now)
                .isDeleted(user.getDeleted())
                .deletedAt(user.getDeletedAt())
                .deletedBy(user.getDeletedBy())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(now)
                .updatedBy(actor)
                .version(user.getVersion())
                .build();

        userRepository.save(updatedUser);
    }
}


