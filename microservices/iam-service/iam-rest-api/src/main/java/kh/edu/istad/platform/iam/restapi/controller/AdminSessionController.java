package kh.edu.istad.platform.iam.restapi.controller;

import kh.edu.istad.platform.iam.domain.dto.AccountSessionResult;
import kh.edu.istad.platform.iam.domain.usecase.ListAllSessionsUseCase;
import kh.edu.istad.platform.iam.domain.usecase.RevokeAccountSessionUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/sessions")
public class AdminSessionController {
    private final ListAllSessionsUseCase list;
    private final RevokeAccountSessionUseCase revoke;
    public AdminSessionController(ListAllSessionsUseCase list, RevokeAccountSessionUseCase revoke) {
        this.list = list; this.revoke = revoke;
    }
    @GetMapping
    @PreAuthorize("hasAnyAuthority('security:read', 'ROLE_ADMIN')")
    public List<AccountSessionResult> all() { return list.execute(); }
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAnyAuthority('security:update', 'ROLE_ADMIN')")
    public void terminate(@PathVariable("sessionId") String sessionId) {
        revoke.execute(sessionId);
    }
}
