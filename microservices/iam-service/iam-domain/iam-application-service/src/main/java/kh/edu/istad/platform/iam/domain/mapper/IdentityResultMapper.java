package kh.edu.istad.platform.iam.domain.mapper;

import kh.edu.istad.platform.iam.domain.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.entity.Identity;

public final class IdentityResultMapper {

    private IdentityResultMapper() {
    }

    public static IdentityResult toResult(Identity identity) {
        if (identity == null) {
            return null;
        }
        return new IdentityResult(
                identity.getId().value(),
                identity.getProvider().name(),
                identity.getProviderSubject(),
                identity.getEmail(),
                identity.getEmailVerified(),
                identity.getDisplayName(),
                identity.getUsername(),
                identity.getPictureUrl(),
                identity.getAttributes(),
                identity.getLinkedAt(),
                identity.getLastLoginAt()
        );
    }
}
