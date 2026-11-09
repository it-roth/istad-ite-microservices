package kh.edu.istad.platform.iam;

import kh.edu.istad.platform.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.service.IamDomainServiceImpl;
import kh.edu.istad.platform.iam.domain.usecase.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.LinkFederatedIdentityUseCase;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;
import kh.edu.istad.platform.iam.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinkFederatedIdentityUseCaseTest {

    @Test
    void linksNewIdentityToTheSignedInUserWithoutEmailAccountMatching() {
        UserRepository userRepository = mock(UserRepository.class);
        GetUserAuthoritiesUseCase authoritiesUseCase = mock(GetUserAuthoritiesUseCase.class);
        UUID userUuid = UUID.randomUUID();
        User user = User.builder()
                .id(new UserId(userUuid))
                .internalId(10L)
                .username("owner")
                .email("owner@example.com")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        when(userRepository.findByUsernameOrEmail("owner")).thenReturn(Optional.of(user));
        when(userRepository.findIdentityIncludingDeletedByProviderAndSubject(SocialProvider.GOOGLE, "subject"))
                .thenReturn(Optional.empty());
        when(authoritiesUseCase.execute(userUuid)).thenReturn(new UserAuthoritiesResult(
                userUuid,
                List.of(),
                List.of(),
                Set.of(),
                Set.of("ROLE_USER")
        ));

        FederatedIdentityCommand command = new FederatedIdentityCommand(
                SocialProvider.GOOGLE,
                "subject",
                "different@example.com",
                true,
                "Google User",
                "google-user",
                null,
                Map.of()
        );

        var result = new LinkFederatedIdentityUseCase(userRepository, authoritiesUseCase, new IamDomainServiceImpl())
                .execute("owner", command);

        assertEquals(userUuid, result.uuid());
        var identityCaptor = org.mockito.ArgumentCaptor.forClass(Identity.class);
        verify(userRepository).saveIdentity(identityCaptor.capture());
        assertEquals(10L, identityCaptor.getValue().getUserId());
        assertEquals(SocialProvider.GOOGLE, identityCaptor.getValue().getProvider());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void refusesToLinkIdentityOwnedByAnotherUser() {
        UserRepository userRepository = mock(UserRepository.class);
        UUID userUuid = UUID.randomUUID();
        User user = User.builder()
                .id(new UserId(userUuid))
                .internalId(10L)
                .username("owner")
                .build();
        Identity existingIdentity = mock(Identity.class);
        when(existingIdentity.getDeleted()).thenReturn(false);
        when(existingIdentity.getUserId()).thenReturn(20L);
        when(userRepository.findByUsernameOrEmail("owner")).thenReturn(Optional.of(user));
        when(userRepository.findIdentityIncludingDeletedByProviderAndSubject(SocialProvider.GOOGLE, "subject"))
                .thenReturn(Optional.of(existingIdentity));

        FederatedIdentityCommand command = new FederatedIdentityCommand(
                SocialProvider.GOOGLE,
                "subject",
                null,
                false,
                null,
                null,
                null,
                Map.of()
        );

        assertThrows(
                IamDomainException.class,
                () -> new LinkFederatedIdentityUseCase(
                        userRepository,
                        mock(GetUserAuthoritiesUseCase.class),
                        new IamDomainServiceImpl()
                )
                        .execute("owner", command)
        );
        verify(userRepository, never()).saveIdentity(any());
    }

    @Test
    void relinksPreviouslyUnlinkedIdentityForItsOriginalOwner() {
        UserRepository userRepository = mock(UserRepository.class);
        GetUserAuthoritiesUseCase authoritiesUseCase = mock(GetUserAuthoritiesUseCase.class);
        UUID userUuid = UUID.randomUUID();
        User user = User.builder()
                .id(new UserId(userUuid))
                .internalId(10L)
                .username("owner")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        Identity identity = mock(Identity.class);
        when(identity.getDeleted()).thenReturn(true);
        when(identity.getUserId()).thenReturn(10L);
        when(userRepository.findByUsernameOrEmail("owner")).thenReturn(Optional.of(user));
        when(userRepository.findIdentityIncludingDeletedByProviderAndSubject(SocialProvider.GOOGLE, "subject"))
                .thenReturn(Optional.of(identity));
        when(userRepository.findIdentitiesByUserId(10L)).thenReturn(List.of());
        when(userRepository.saveIdentity(identity)).thenReturn(identity);
        when(authoritiesUseCase.execute(userUuid)).thenReturn(new UserAuthoritiesResult(
                userUuid,
                List.of(),
                List.of(),
                Set.of(),
                Set.of("ROLE_USER")
        ));

        FederatedIdentityCommand command = new FederatedIdentityCommand(
                SocialProvider.GOOGLE,
                "subject",
                "owner@example.com",
                true,
                "Google User",
                "google-user",
                null,
                Map.of()
        );

        new LinkFederatedIdentityUseCase(userRepository, authoritiesUseCase, new IamDomainServiceImpl())
                .execute("owner", command);

        verify(identity).setDeleted(false);
        verify(identity).setDeletedAt(null);
        verify(identity).setDeletedBy(null);
        verify(userRepository).saveIdentity(identity);
    }
}
