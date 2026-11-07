package kh.edu.istad.platform.iam.domain.mapper;

import kh.edu.istad.platform.iam.domain.dto.RoleResult;
import kh.edu.istad.platform.iam.domain.dto.RoleSummary;
import kh.edu.istad.platform.iam.domain.entity.Role;

public final class RoleResultMapper {

    private RoleResultMapper() {
    }

    public static RoleResult toResult(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleResult(
                role.getId().value(),
                role.getName(),
                role.getDescription(),
                role.getSystem(),
                role.getActive(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    public static RoleSummary toSummary(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleSummary(
                role.getId().value(),
                role.getName(),
                role.getSystem(),
                role.getActive()
        );
    }
}
