package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.RoleFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.RoleSummary;
import kh.edu.istad.platform.iam.domain.port.out.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListRolesUseCase {

    private final RoleRepository roleRepository;

    public PageResult<RoleSummary> execute(RoleFilterQuery query) {
        log.info("Listing roles with query: {}", query);
        return roleRepository.findAll(query);
    }
}

