package kh.edu.istad.platform.iam.restapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.domain.dto.*;
import kh.edu.istad.platform.iam.domain.usecase.*;
import kh.edu.istad.platform.iam.restapi.dto.SecurityConfigurationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class AccountSecurityController {
    private final ListAccountSessionsUseCase listSessions;
    private final RevokeAccountSessionUseCase revokeSession;
    private final ListLoginHistoryUseCase loginHistory;
    private final ListAccountActivityUseCase activity;
    private final LoadUserForAuthenticationUseCase loadUser;
    private final kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository accountSecurityRepository;

    @GetMapping("/sessions")
    public List<AccountSessionResult> sessions(Authentication auth, HttpServletRequest request) {
        UUID uid = userUuid(auth);
        var session = request.getSession(false);
        String currentSessionId = session == null ? null : session.getId();
        if (currentSessionId != null) {
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isBlank()) clientIp = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(clientIp) || "::1".equals(clientIp)) clientIp = "127.0.0.1";
            String userAgent = request.getHeader("User-Agent");
            accountSecurityRepository.touchLastLogin(uid);
            accountSecurityRepository.registerSession(uid, currentSessionId, userAgent, userAgent, userAgent, clientIp);
        }
        return listSessions.execute(uid, currentSessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> revoke(Authentication auth, @PathVariable("sessionId") String sessionId, HttpServletRequest request) {
        UUID uid = userUuid(auth);
        revokeSession.execute(uid, sessionId);
        try {
            String ip = extractClientIp(request);
            accountSecurityRepository.recordActivity(uid, "SESSION_TERMINATED",
                    "Terminated device session (" + (sessionId.length() > 8 ? sessionId.substring(0, 8) + "..." : sessionId) + ")",
                    auth.getName(), ip);
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> revokeOthers(Authentication auth, HttpServletRequest request) {
        UUID uid = userUuid(auth);
        var session = request.getSession(false);
        revokeSession.executeOthers(uid, session == null ? "" : session.getId());
        try {
            String ip = extractClientIp(request);
            accountSecurityRepository.recordActivity(uid, "SESSION_TERMINATED",
                    "Signed out of all other active sessions", auth.getName(), ip);
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/login-history")
    public List<LoginHistoryResult> history(Authentication auth, @RequestParam(name = "limit", defaultValue = "50") int limit, HttpServletRequest request) {
        UUID uid = userUuid(auth);
        List<LoginHistoryResult> results = loginHistory.execute(uid, limit);
        if (results.isEmpty()) {
            accountSecurityRepository.touchLastLogin(uid);
            String clientIp = extractClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            accountSecurityRepository.recordLoginAttempt(uid, auth.getName(), true, "Password", userAgent, clientIp);
            var session = request.getSession(false);
            if (session != null) {
                accountSecurityRepository.registerSession(uid, session.getId(), "Chrome", "Desktop", userAgent, clientIp);
            }
            results = loginHistory.execute(uid, limit);
        }
        return results;
    }

    @GetMapping({"/activity", "/security-activity"})
    public List<AccountActivityResult> activity(Authentication auth, @RequestParam(name = "limit", defaultValue = "50") int limit) {
        return activity.execute(userUuid(auth), limit);
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1 (Localhost)";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            ip = ip.split(",")[0].trim();
        }
        if (ip == null || ip.isBlank()) {
            String xr = request.getHeader("X-Real-IP");
            if (xr != null && !xr.isBlank() && !"unknown".equalsIgnoreCase(xr)) {
                ip = xr.trim();
            }
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1") || ip.equals("127.0.0.1")) {
            return "127.0.0.1 (Localhost)";
        }
        return ip;
    }

    private UUID userUuid(Authentication auth) {
        if (auth == null || auth.getName() == null) throw new AccessDeniedException("Not authenticated");
        try { return UUID.fromString(auth.getName()); }
        catch (IllegalArgumentException ex) { return loadUser.execute(auth.getName()).uuid(); }
    }
}
