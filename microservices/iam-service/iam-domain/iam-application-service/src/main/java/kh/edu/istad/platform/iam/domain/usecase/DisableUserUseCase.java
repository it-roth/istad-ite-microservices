package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DisableUserUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;

    public void execute(UUID userUuid) {
        log.info("Disabling user UUID: {}", userUuid);
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IamDomainException("User not found: " + userUuid));

        LocalDateTime now = LocalDateTime.now();
        String actor = currentActorProvider.getCurrentActor();

        User updatedUser = User.builder()
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
                .enabled(false)
                .accountNonExpired(user.getAccountNonExpired())
                .accountNonLocked(user.getAccountNonLocked())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lastLoginAt(user.getLastLoginAt())
                .passwordChangedAt(user.getPasswordChangedAt())
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


