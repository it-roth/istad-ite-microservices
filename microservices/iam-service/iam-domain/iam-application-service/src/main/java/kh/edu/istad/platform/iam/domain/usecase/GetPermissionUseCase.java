package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.PermissionResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetPermissionUseCase {

    private final PermissionRepository permissionRepository;

    public PermissionResult execute(UUID permissionUuid) {
        log.info("Fetching permission by UUID: {}", permissionUuid);
        Permission permission = permissionRepository.findByUuid(permissionUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("Permission not found: " + permissionUuid);
                });
        return PermissionResultMapper.toResult(permission);
    }
}

