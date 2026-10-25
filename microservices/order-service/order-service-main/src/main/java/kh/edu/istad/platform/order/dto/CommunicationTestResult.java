package kh.edu.istad.platform.order.dto;

import java.util.Map;

public record CommunicationTestResult(
        String testMode,
        boolean allServicesReachable,
        long totalDurationMs,
        Map<String, ServiceCheckResult> results,
        String summary
) {
    public record ServiceCheckResult(
            String serviceName,
            String targetUrl,
            boolean success,
            int statusCode,
            long latencyMs,
            Object details,
            String note
    ) {}
}
