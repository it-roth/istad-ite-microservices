package kh.edu.istad.platform.order.domain.dto;

import java.util.Map;

public record CommunicationTestResult(
        String communicationMode,
        boolean allServicesReachable,
        long totalDurationMs,
        Map<String, ServiceCheckResult> serviceChecks,
        String summary
) {
    public record ServiceCheckResult(
            String serviceName,
            String url,
            boolean success,
            int statusCode,
            long responseTimeMs,
            Object responseBody,
            String note
    ) {
    }
}
