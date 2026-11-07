package kh.edu.istad.platform.iam.domain.mapper;

import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.dto.UserSummary;
import kh.edu.istad.platform.iam.domain.entity.User;

public final class UserResultMapper {

    private UserResultMapper() {
    }

    public static UserResult toResult(User user) {
        if (user == null) {
            return null;
        }
        return new UserResult(
                user.getId().value(),
                user.getUsername(),
                user.getEmail(),
                user.getEmailVerified(),
                user.getGivenName(),
                user.getFamilyName(),
                user.getMiddleName(),
                user.getNickname(),
                user.getGender(),
                user.getBirthdate(),
                user.getProfileUrl(),
                user.getPictureUrl(),
                user.getWebsiteUrl(),
                user.getZoneinfo(),
                user.getLocale(),
                user.getPhoneNumber(),
                user.getPhoneNumberVerified(),
                user.getEnabled(),
                user.getAccountNonLocked(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static UserSummary toSummary(User user) {
        if (user == null) {
            return null;
        }
        return new UserSummary(
                user.getId().value(),
                user.getUsername(),
                user.getEmail(),
                user.getGivenName(),
                user.getFamilyName(),
                user.getEnabled(),
                user.getCreatedAt()
        );
    }
}
