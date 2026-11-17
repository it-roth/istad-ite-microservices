package kh.edu.istad.platform.iam.restapi.controller;

import kh.edu.istad.platform.iam.domain.dto.AdminAuditLogResult;
import kh.edu.istad.platform.iam.domain.usecase.ListAllAuditLogsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
public class AdminAuditController {

    private final ListAllAuditLogsUseCase listAllAuditLogsUseCase;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH);

    public record AuditLogDto(
            String id,
            String whenRelative,
            String whenDate,
            String action,
            String changed,
            String by,
            String type
    ) {}

    @GetMapping
    @PreAuthorize("hasAnyAuthority('security:read', 'user:read', 'ROLE_ADMIN')")
    public List<AuditLogDto> list(@RequestParam(name = "limit", defaultValue = "100") int limit) {
        List<AdminAuditLogResult> items = listAllAuditLogsUseCase.execute(limit);
        OffsetDateTime now = OffsetDateTime.now();

        return items.stream().map(item -> {
            String relative = formatRelativeTime(item.occurredAt(), now);
            String formattedDate = item.occurredAt() != null ? item.occurredAt().format(DATE_FORMATTER) : "—";
            String action = formatAction(item.eventType(), item.message());
            String type = determineType(item.eventType(), action);
            String changed = item.targetSubject() != null && !item.targetSubject().isBlank()
                    ? item.targetSubject() : "system";
            String by = item.actor() != null && !item.actor().isBlank() ? item.actor() : "system";

            return new AuditLogDto(
                    String.valueOf(item.id()),
                    relative,
                    formattedDate,
                    action,
                    changed,
                    by,
                    type
            );
        }).toList();
    }

    private String formatRelativeTime(OffsetDateTime occurredAt, OffsetDateTime now) {
        if (occurredAt == null) return "Unknown";
        Duration d = Duration.between(occurredAt, now);
        long seconds = d.getSeconds();
        if (seconds < 0) seconds = 0;
        if (seconds < 60) return "Just now";
        long minutes = d.toMinutes();
        if (minutes < 60) return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        long hours = d.toHours();
        if (hours < 24) return hours + (hours == 1 ? " hour ago" : " hours ago");
        long days = d.toDays();
        if (days == 1) return "Yesterday";
        if (days < 7) return days + " days ago";
        if (days < 30) return (days / 7) + ((days / 7) == 1 ? " week ago" : " weeks ago");
        return (days / 30) + ((days / 30) == 1 ? " month ago" : " months ago");
    }

    private String formatAction(String eventType, String message) {
        if (eventType == null) return message != null ? message : "Activity recorded";
        String upper = eventType.toUpperCase(Locale.ROOT);
        if (upper.contains("USER_CREATE") || upper.contains("CREATE_USER")) return "User created";
        if (upper.contains("ROLE") || upper.contains("ROLES_ASSIGNED")) return "Roles assigned";
        if (upper.contains("PASSWORD")) return "Password reset";
        if (upper.contains("SESSION_TERMINATED")) return "Session terminated";
        if (upper.contains("IDENTITY")) return "Identity linked";
        if (upper.contains("PROFILE")) return "Profile updated";
        if (upper.contains("SECURITY")) return "Security configured";
        if (upper.contains("CLIENT")) return "Client registered";
        return message != null && !message.isBlank() ? message : eventType;
    }

    private String determineType(String eventType, String action) {
        String combined = ((eventType != null ? eventType : "") + " " + (action != null ? action : "")).toLowerCase(Locale.ROOT);
        if (combined.contains("client")) return "client";
        if (combined.contains("role") || combined.contains("perm")) return "role";
        return "user";
    }
}
