package kh.edu.istad.platform.iam.domain.mapper;

import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;

public final class PermissionResultMapper {

    private PermissionResultMapper() {
    }

    public static PermissionResult toResult(Permission permission) {
        if (permission == null) {
            return null;
        }
        return new PermissionResult(
                permission.getId().value(),
                permission.getName(),
                permission.getResource(),
                permission.getAction(),
                permission.getDescription(),
                permission.getSystem(),
                permission.getActive(),
                permission.getCreatedAt()
        );
    }
}
