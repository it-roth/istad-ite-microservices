package kh.edu.istad.platform.iam.domain.usecase;

import kh.edu.istad.platform.iam.domain.dto.PageResult;
import kh.edu.istad.platform.iam.domain.dto.UserFilterQuery;
import kh.edu.istad.platform.iam.domain.dto.UserSummary;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListUsersUseCase {

    private final UserRepository userRepository;

    public PageResult<UserSummary> execute(UserFilterQuery query) {
        log.info("Listing users with query: {}", query);
        return userRepository.findAll(query);
    }
}


