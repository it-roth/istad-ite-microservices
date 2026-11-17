package kh.edu.istad.platform.iam.infrastructure.adapter;

import kh.edu.istad.platform.iam.domain.dto.*;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Repository
public class AccountSecurityRepositoryAdapter implements AccountSecurityRepository {
    private final JdbcTemplate jdbc;
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public AccountSecurityRepositoryAdapter(
            JdbcTemplate jdbc,
            @Autowired(required = false) FindByIndexNameSessionRepository<? extends Session> sessionRepository
    ) {
        this.jdbc = jdbc;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<AccountSessionResult> findSessions(UUID userId, String currentSessionId) {
        if (sessionRepository != null) {
            try {
                String username = jdbc.queryForObject("SELECT username FROM users WHERE uuid = ?", String.class, userId);
                if (username != null) {
                    var userSessions = sessionRepository.findByPrincipalName(username);
                    if (userSessions != null && !userSessions.isEmpty()) {
                        return userSessions.values().stream().map(s -> {
                            String browser = s.getAttribute("browser") != null ? (String) s.getAttribute("browser") : "Chrome";
                            String device = s.getAttribute("device") != null ? (String) s.getAttribute("device") : "Desktop";
                            String ip = s.getAttribute("ipAddress") != null ? (String) s.getAttribute("ipAddress") : "127.0.0.1";
                            String loc = s.getAttribute("location") != null ? (String) s.getAttribute("location") : "Phnom Penh, Cambodia";
                            OffsetDateTime created = s.getCreationTime().atOffset(ZoneOffset.UTC);
                            OffsetDateTime last = s.getLastAccessedTime().atOffset(ZoneOffset.UTC);
                            return new AccountSessionResult(s.getId(), s.getId().equals(currentSessionId),
                                    browser, device, ip, loc, created, last);
                        }).toList();
                    }
                }
            } catch (Exception ignored) {}
        }
        return jdbc.query("""
                SELECT id, browser, device, ip_address, location, created_at, last_activity
                FROM iam_sessions WHERE user_uuid = ? AND revoked_at IS NULL
                ORDER BY last_activity DESC
                """, (rs, n) -> new AccountSessionResult(
                rs.getString("id"), rs.getString("id").equals(currentSessionId),
                rs.getString("browser"), rs.getString("device"), rs.getString("ip_address"),
                rs.getString("location"), rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("last_activity", OffsetDateTime.class)), userId);
    }

    @Override
    public List<AccountSessionResult> findAllSessions() {
        return jdbc.query("""
                SELECT id, browser, device, ip_address, location, created_at, last_activity
                FROM iam_sessions WHERE revoked_at IS NULL ORDER BY last_activity DESC
                """, (rs, n) -> new AccountSessionResult(rs.getString("id"), false,
                rs.getString("browser"), rs.getString("device"), rs.getString("ip_address"),
                rs.getString("location"), rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("last_activity", OffsetDateTime.class)));
    }

    @Override
    @Transactional
    public void revokeSession(UUID userId, String sessionId) {
        if (sessionRepository != null) {
            try {
                sessionRepository.deleteById(sessionId);
            } catch (Exception ignored) {}
        }
        jdbc.update("UPDATE iam_sessions SET revoked_at = CURRENT_TIMESTAMP WHERE user_uuid = ? AND id = ?",
                userId, sessionId);
    }

    @Override
    public void revokeSession(String sessionId) {
        if (sessionRepository != null) {
            try {
                sessionRepository.deleteById(sessionId);
            } catch (Exception ignored) {}
        }
        jdbc.update("UPDATE iam_sessions SET revoked_at = CURRENT_TIMESTAMP WHERE id = ?", sessionId);
    }

    @Override
    @Transactional
    public void revokeOtherSessions(UUID userId, String currentSessionId) {
        if (sessionRepository != null) {
            try {
                String username = jdbc.queryForObject("SELECT username FROM users WHERE uuid = ?", String.class, userId);
                if (username != null) {
                    var userSessions = sessionRepository.findByPrincipalName(username);
                    if (userSessions != null) {
                        for (String sid : userSessions.keySet()) {
                            if (!sid.equals(currentSessionId)) {
                                sessionRepository.deleteById(sid);
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        jdbc.update("UPDATE iam_sessions SET revoked_at = CURRENT_TIMESTAMP WHERE user_uuid = ? AND id <> ? AND revoked_at IS NULL",
                userId, currentSessionId);
    }

    @Override
    public List<LoginHistoryResult> findLoginHistory(UUID userId, int limit) {
        return jdbc.query("""
                SELECT id, success, method, user_agent, ip_address, location, occurred_at
                FROM iam_login_history WHERE user_uuid = ? ORDER BY occurred_at DESC LIMIT ?
                """, (rs, n) -> new LoginHistoryResult(rs.getLong("id"), rs.getBoolean("success"),
                rs.getString("method"), rs.getString("user_agent"), rs.getString("ip_address"),
                rs.getString("location"), rs.getObject("occurred_at", OffsetDateTime.class)), userId, safeLimit(limit));
    }

    @Override
    public List<AccountActivityResult> findActivity(UUID userId, int limit) {
        return jdbc.query("""
                SELECT id, event_type, message, ip_address, actor, occurred_at
                FROM iam_account_activity
                WHERE user_uuid = ?
                  AND UPPER(event_type) NOT LIKE '%LOGIN%'
                  AND UPPER(event_type) NOT LIKE '%SIGNIN%'
                ORDER BY occurred_at DESC LIMIT ?
                """, (rs, n) -> new AccountActivityResult(rs.getLong("id"), rs.getString("event_type"),
                rs.getString("message"), rs.getString("ip_address"), rs.getString("actor"),
                rs.getObject("occurred_at", OffsetDateTime.class)), userId, safeLimit(limit));
    }

    @Override
    public List<AdminAuditLogResult> findAllActivity(int limit) {
        return jdbc.query("""
                SELECT a.id, a.event_type, a.message, a.actor, a.ip_address, a.occurred_at,
                       COALESCE(u.username, u.email, a.actor, 'system') AS target_subject
                FROM iam_account_activity a
                LEFT JOIN users u ON a.user_uuid = u.uuid
                ORDER BY a.occurred_at DESC LIMIT ?
                """, (rs, n) -> new AdminAuditLogResult(
                rs.getLong("id"),
                rs.getString("event_type"),
                rs.getString("message"),
                rs.getString("actor"),
                rs.getString("ip_address"),
                rs.getString("target_subject"),
                rs.getObject("occurred_at", OffsetDateTime.class)), safeLimit(limit));
    }

    @Override
    public SecurityConfigurationResult getSecurityConfiguration() {
        return jdbc.queryForObject("""
                SELECT mfa_required, max_failed_login_attempts, session_timeout_minutes,
                       password_minimum_length, password_require_uppercase,
                       password_require_number, password_require_symbol
                FROM iam_security_configuration WHERE id = 1
                """, (rs, n) -> new SecurityConfigurationResult(rs.getBoolean(1), rs.getInt(2),
                rs.getInt(3), rs.getInt(4), rs.getBoolean(5), rs.getBoolean(6), rs.getBoolean(7)));
    }

    @Override
    @Transactional
    public SecurityConfigurationResult updateSecurityConfiguration(SecurityConfigurationResult c) {
        jdbc.update("""
                UPDATE iam_security_configuration SET mfa_required=?, max_failed_login_attempts=?,
                session_timeout_minutes=?, password_minimum_length=?, password_require_uppercase=?,
                password_require_number=?, password_require_symbol=?, updated_at=CURRENT_TIMESTAMP WHERE id=1
                """, c.mfaRequired(), c.maxFailedLoginAttempts(), c.sessionTimeoutMinutes(),
                c.passwordMinimumLength(), c.passwordRequireUppercase(), c.passwordRequireNumber(),
                c.passwordRequireSymbol());
        return getSecurityConfiguration();
    }

    @Override
    public void recordLoginAttempt(UUID userId, String principal, boolean success, String method,
                                   String userAgent, String ipAddress) {
        jdbc.update("INSERT INTO iam_login_history(user_uuid, principal_name, success, method, user_agent, ip_address) VALUES (?, ?, ?, ?, ?, ?)",
                userId, principal, success, method, userAgent, ipAddress);
    }

    @Override
    public void recordActivity(UUID userId, String eventType, String message, String actor, String ipAddress) {
        jdbc.update("INSERT INTO iam_account_activity(user_uuid, event_type, message, actor, ip_address) VALUES (?, ?, ?, ?, ?)",
                userId, eventType, message, actor, ipAddress);
    }

    @Override
    public void registerSession(UUID userId, String sessionId, String browser, String device,
                                String userAgent, String ipAddress) {
        // Session tracking is managed automatically by Spring Session Redis
    }

    @Override
    public void touchLastLogin(UUID userId) {
        jdbc.update("UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE uuid = ?", userId);
    }

    private int safeLimit(int limit) { return Math.max(1, Math.min(limit, 200)); }
}
