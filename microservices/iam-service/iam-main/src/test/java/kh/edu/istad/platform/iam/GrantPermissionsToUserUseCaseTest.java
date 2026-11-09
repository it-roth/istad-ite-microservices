package kh.edu.istad.platform.iam;

import kh.edu.istad.platform.iam.domain.dto.GrantPermissionsToUserCommand;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainService;
import kh.edu.istad.platform.iam.domain.usecase.GrantPermissionsToUserUseCase;
import kh.edu.istad.platform.iam.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GrantPermissionsToUserUseCaseTest {

    @Test
    void rejectsUnknownPermissionEffectInsteadOfGrantingPermission() {
        UserRepository userRepository = mock(UserRepository.class);
        PermissionRepository permissionRepository = mock(PermissionRepository.class);
        CurrentActorProvider actorProvider = mock(CurrentActorProvider.class);
        IamDomainService domainService = mock(IamDomainService.class);
        UUID userUuid = UUID.randomUUID();
        User user = User.builder()
                .id(new UserId(userUuid))
                .internalId(10L)
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(permissionRepository.findAllByUuids(List.of())).thenReturn(List.of());

        GrantPermissionsToUserCommand command = new GrantPermissionsToUserCommand(
                userUuid,
                List.of(),
                "GRAANT",
                null,
                null
        );

        assertThrows(
                IamDomainException.class,
                () -> new GrantPermissionsToUserUseCase(
                        userRepository,
                        permissionRepository,
                        actorProvider,
                        domainService
                ).execute(command)
        );
        verifyNoInteractions(userRepository, permissionRepository, actorProvider, domainService);
    }
}
