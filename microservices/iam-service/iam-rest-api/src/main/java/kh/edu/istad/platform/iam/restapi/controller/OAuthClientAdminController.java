package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.CreateOAuthClientCommand;
import kh.edu.istad.platform.iam.domain.usecase.ManageOAuthClientsUseCase;
import kh.edu.istad.platform.iam.restapi.dto.CreateOAuthClientRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin/oauth-clients")
@RequiredArgsConstructor
public class OAuthClientAdminController {
    private final ManageOAuthClientsUseCase useCase;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('client:read', 'ROLE_ADMIN')")
    public Object list() { return useCase.list(); }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('client:read', 'ROLE_ADMIN')")
    public Object get(@PathVariable("id") String id) { return useCase.get(id); }
    @PostMapping
    @PreAuthorize("hasAnyAuthority('client:create', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(@Valid @RequestBody CreateOAuthClientRequest r) {
        return useCase.create(new CreateOAuthClientCommand(
                r.clientId(), r.clientName(), r.clientSecret(),
                r.grantTypes(), r.authenticationMethods(), r.scopes(),
                r.redirectUris(), r.postLogoutRedirectUris(),
                r.requireAuthorizationConsent(),
                r.accessTokenTimeToLiveMinutes(),
                r.refreshTokenTimeToLiveMinutes()));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('client:update', 'ROLE_ADMIN')")
    public Object update(@PathVariable("id") String id, @Valid @RequestBody CreateOAuthClientRequest r) {
        return useCase.update(id, new CreateOAuthClientCommand(
                r.clientId(), r.clientName(), r.clientSecret(),
                r.grantTypes(), r.authenticationMethods(), r.scopes(),
                r.redirectUris(), r.postLogoutRedirectUris(),
                r.requireAuthorizationConsent(),
                r.accessTokenTimeToLiveMinutes(),
                r.refreshTokenTimeToLiveMinutes()));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('client:delete', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) { useCase.delete(id); }
}
