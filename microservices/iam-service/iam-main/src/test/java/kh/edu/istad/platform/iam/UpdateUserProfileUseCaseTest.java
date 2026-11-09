package kh.edu.istad.platform.iam;

import kh.edu.istad.platform.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.usecase.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iam.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateUserProfileUseCaseTest {

    @Test
    void changingEmailNormalizesItAndRequiresVerification() {
        UserRepository userRepository = mock(UserRepository.class);
        CurrentActorProvider actorProvider = mock(CurrentActorProvider.class);
        UUID userUuid = UUID.randomUUID();
        User user = User.builder()
                .id(new UserId(userUuid))
                .internalId(1L)
                .username("dara")
                .email("old@example.com")
                .emailVerified(true)
                .enabled(true)
                .accountNonLocked(true)
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(actorProvider.getCurrentActor()).thenReturn("dara");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = new UpdateUserProfileUseCase(userRepository, actorProvider).execute(
                new UpdateUserProfileCommand(userUuid, " New@Example.com ", null, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, null, null, null)
        );

        assertEquals("new@example.com", result.email());
        assertFalse(result.emailVerified());
    }

    @Test
    void rejectsEmailAlreadyUsedByAnotherUser() {
        UserRepository userRepository = mock(UserRepository.class);
        CurrentActorProvider actorProvider = mock(CurrentActorProvider.class);
        UUID userUuid = UUID.randomUUID();
        User user = User.builder()
                .id(new UserId(userUuid))
                .internalId(1L)
                .email("old@example.com")
                .emailVerified(true)
                .build();
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("used@example.com")).thenReturn(true);

        assertThrows(
                IamDomainException.class,
                () -> new UpdateUserProfileUseCase(userRepository, actorProvider).execute(
                        new UpdateUserProfileCommand(userUuid, "used@example.com", null, null, null, null,
                                null, null, null, null, null, null, null, null, null, null, null, null, null)
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }
}
