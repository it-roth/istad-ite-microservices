package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.PermissionFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListPermissionsUseCase {

    private final PermissionRepository permissionRepository;

    public PageResult<PermissionResult> execute(PermissionFilterQuery query) {
        log.info("Listing permissions with query: {}", query);
        return permissionRepository.findAll(query);
    }
}

