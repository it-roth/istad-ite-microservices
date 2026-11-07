package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.dto.UserResult;
import kh.edu.istad.platform.iam.domain.usecase.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.RegisterUserUseCase;
import kh.edu.istad.platform.iam.restapi.dto.RegisterUserRequest;
import kh.edu.istad.platform.iam.restapi.dto.UserResponse;
import kh.edu.istad.platform.iam.restapi.dto.UserRoleSummaryResponse;
import kh.edu.istad.platform.iam.restapi.mapper.IamWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final IamWebMapper iamWebMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        RegisterUserCommand command = iamWebMapper.toCommand(request);
        UserResult userResult = registerUserUseCase.execute(command);
        UserAuthoritiesResult authoritiesResult = getUserAuthoritiesUseCase.execute(userResult.uuid());

        List<UserRoleSummaryResponse> roleSummaries = List.of();
        if (authoritiesResult.roles() != null) {
            roleSummaries = authoritiesResult.roles().stream()
                    .map(r -> {
                        return new UserRoleSummaryResponse(r.uuid(), r.name());
                    })
                    .toList();
        }

        UserResponse response = iamWebMapper.toResponse(userResult, roleSummaries);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
