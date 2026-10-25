package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.platform.order.domain.dto.*;
import kh.edu.istad.platform.order.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.order.domain.port.out.OrderServiceGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OrderServiceWebClientAdapter implements OrderServiceGateway, PaymentRepository {
    private final WebClient directWebClient;
    private final WebClient loadBalancedWebClient;

    @Value("${microservices.hardcoded.customer-url:http://localhost:9091}")
    private String hardcodedCustomerUrl;
    @Value("${microservices.hardcoded.business-url:http://localhost:9093}")
    private String hardcodedBusinessUrl;
    @Value("${microservices.hardcoded.payment-url:http://localhost:9094}")
    private String hardcodedPaymentUrl;
    @Value("${microservices.eureka.customer-url:http://customer-service}")
    private String eurekaCustomerUrl;
    @Value("${microservices.eureka.business-url:http://business-service}")
    private String eurekaBusinessUrl;
    @Value("${microservices.eureka.payment-url:http://payment-service}")
    private String eurekaPaymentUrl;

    public OrderServiceWebClientAdapter(
            @Qualifier("directWebClient") WebClient directWebClient,
            @Qualifier("loadBalancedWebClient") WebClient loadBalancedWebClient
    ) {
        this.directWebClient = directWebClient;
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    @Override
    public PaymentServiceResponse processPayment(PaymentCommand command, String discoveryMode) {
        boolean useEureka = !"HARD_CODED".equalsIgnoreCase(discoveryMode);
        WebClient client = selectClient(useEureka);
        String baseUrl = useEureka ? eurekaPaymentUrl : hardcodedPaymentUrl;
        return client.post()
                .uri(baseUrl + "/api/payments")
                .bodyValue(command)
                .retrieve()
                .bodyToMono(PaymentServiceResponse.class)
                .block(Duration.ofSeconds(4));
    }

    @Override
    public CommunicationTestResult testCommunication(boolean useEureka) {
        long start = System.currentTimeMillis();
        WebClient client = selectClient(useEureka);
        String customerUrl = (useEureka ? eurekaCustomerUrl : hardcodedCustomerUrl) + "/api/customers";
        String businessUrl = (useEureka ? eurekaBusinessUrl : hardcodedBusinessUrl) + "/api/businesses";
        String paymentUrl = (useEureka ? eurekaPaymentUrl : hardcodedPaymentUrl) + "/api/payments";

        return Mono.zip(
                        pingService(client, "customer-service", customerUrl, "Customer microservice health & endpoints"),
                        pingService(client, "business-service", businessUrl, "Business microservice catalog & products"),
                        pingService(client, "payment-service", paymentUrl, "Payment microservice transaction processor"))
                .map(results -> {
                    Map<String, CommunicationTestResult.ServiceCheckResult> checks = new LinkedHashMap<>();
                    checks.put("customerService", results.getT1());
                    checks.put("businessService", results.getT2());
                    checks.put("paymentService", results.getT3());
                    boolean allOk = results.getT1().success() && results.getT2().success()
                            && results.getT3().success();
                    String mode = useEureka
                            ? "EUREKA_DISCOVERY_AND_LOAD_BALANCING"
                            : "HARD_CODED_SERVICE_ADDRESSES";
                    String summary = useEureka
                            ? "Dynamic Service Discovery via Eureka Server [8761] and Spring Cloud LoadBalancer resolved all service names."
                            : "Direct REST communication executed against static, hard-coded IP:Port configurations.";
                    return new CommunicationTestResult(
                            mode, allOk, System.currentTimeMillis() - start, checks, summary);
                })
                .block(Duration.ofSeconds(4));
    }

    private WebClient selectClient(boolean useEureka) {
        return useEureka ? loadBalancedWebClient : directWebClient;
    }

    private Mono<CommunicationTestResult.ServiceCheckResult> pingService(
            WebClient client, String serviceName, String url, String note) {
        long start = System.currentTimeMillis();
        return client.get()
                .uri(url)
                .exchangeToMono(response -> {
                    long duration = System.currentTimeMillis() - start;
                    int statusCode = response.statusCode().value();
                    boolean success = response.statusCode().is2xxSuccessful();
                    return response.bodyToMono(Object.class)
                            .defaultIfEmpty("No content")
                            .map(body -> new CommunicationTestResult.ServiceCheckResult(
                                    serviceName, url, success, statusCode, duration, body, note));
                })
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(error -> Mono.just(new CommunicationTestResult.ServiceCheckResult(
                        serviceName,
                        url,
                        false,
                        503,
                        System.currentTimeMillis() - start,
                        error.getMessage(),
                        "Connection failed: " + error.getMessage())));
    }
}
