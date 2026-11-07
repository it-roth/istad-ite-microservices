package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.RoleResult;
import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.mapper.RoleResultMapper;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetRoleUseCase {

    private final RoleRepository roleRepository;

    public RoleResult execute(UUID roleUuid) {
        log.info("Fetching role by UUID: {}", roleUuid);
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> {
                    return new IamDomainException("Role not found: " + roleUuid);
                });
        return RoleResultMapper.toResult(role);
    }
}

