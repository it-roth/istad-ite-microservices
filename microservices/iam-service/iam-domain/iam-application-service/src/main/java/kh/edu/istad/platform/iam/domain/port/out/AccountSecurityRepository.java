package kh.edu.istad.platform.iam.domain.port.out;

import kh.edu.istad.platform.iam.domain.dto.*;

import java.util.List;
import java.util.UUID;

public interface AccountSecurityRepository {
    List<AccountSessionResult> findSessions(UUID userId, String currentSessionId);
    List<AccountSessionResult> findAllSessions();
    void revokeSession(UUID userId, String sessionId);
    void revokeSession(String sessionId);
    void revokeOtherSessions(UUID userId, String currentSessionId);
    List<LoginHistoryResult> findLoginHistory(UUID userId, int limit);
    List<AccountActivityResult> findActivity(UUID userId, int limit);
    List<AdminAuditLogResult> findAllActivity(int limit);
    SecurityConfigurationResult getSecurityConfiguration();
    SecurityConfigurationResult updateSecurityConfiguration(SecurityConfigurationResult configuration);
    void recordLoginAttempt(UUID userId, String principal, boolean success, String method,
                            String userAgent, String ipAddress);
    void recordActivity(UUID userId, String eventType, String message, String actor, String ipAddress);
    void registerSession(UUID userId, String sessionId, String browser, String device,
                         String userAgent, String ipAddress);
    void touchLastLogin(UUID userId);
}
