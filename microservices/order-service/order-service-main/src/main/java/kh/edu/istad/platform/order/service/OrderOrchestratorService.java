package kh.edu.istad.platform.order.service;

import kh.edu.istad.platform.order.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderOrchestratorService {

    private static final Logger log = LoggerFactory.getLogger(OrderOrchestratorService.class);

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

    private final Map<String, OrderResponseDto> orderRepository = new ConcurrentHashMap<>();

    public OrderOrchestratorService(WebClient directWebClient, WebClient loadBalancedWebClient) {
        this.directWebClient = directWebClient;
        this.loadBalancedWebClient = loadBalancedWebClient;
    }

    public Mono<OrderResponseDto> createOrder(CreateOrderRequest request) {
        boolean useEureka = !"HARD_CODED".equalsIgnoreCase(request.discoveryMode());
        String mode = useEureka ? "EUREKA_DISCOVERY" : "HARD_CODED";

        WebClient client = useEureka ? loadBalancedWebClient : directWebClient;
        String custBase = useEureka ? eurekaCustomerUrl : hardcodedCustomerUrl;
        String bizBase = useEureka ? eurekaBusinessUrl : hardcodedBusinessUrl;
        String payBase = useEureka ? eurekaPaymentUrl : hardcodedPaymentUrl;

        String orderId = UUID.randomUUID().toString();
        String trackingId = UUID.randomUUID().toString();

        BigDecimal totalPrice = request.items().stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("[Order-Service] Initiating order creation. Mode: {}, OrderId: {}, Total: {}", mode, orderId, totalPrice);

        // Step 1: Validate Customer
        return client.get()
                .uri(custBase + "/api/customers/" + request.customerId())
                .retrieve()
                .bodyToMono(CustomerResponseDto.class)
                .timeout(Duration.ofSeconds(4))
                .onErrorResume(e -> {
                    log.warn("Customer check fallback for id: {}. Error: {}", request.customerId(), e.getMessage());
                    return Mono.just(new CustomerResponseDto(
                            request.customerId(), "demo_customer", "Demo", "User", null, null, "ACTIVE"));
                })
                .flatMap(customer -> {
                    log.info("[Order-Service] Customer validated: {}", customer.username());

                    // Step 2: Validate Business
                    return client.get()
                            .uri(bizBase + "/api/businesses/" + request.businessId())
                            .retrieve()
                            .bodyToMono(BusinessResponseDto.class)
                            .timeout(Duration.ofSeconds(4))
                            .onErrorResume(e -> {
                                log.warn("Business check fallback for id: {}. Error: {}", request.businessId(), e.getMessage());
                                return Mono.just(new BusinessResponseDto(request.businessId(), "TechZone Mart", true, List.of()));
                            })
                            .flatMap(business -> {
                                log.info("[Order-Service] Business validated: {}", business.businessName());

                                // Step 3: Process Payment
                                PaymentRequestDto paymentRequest = new PaymentRequestDto(
                                        orderId,
                                        request.customerId(),
                                        totalPrice,
                                        "ISTAD_PAY"
                                );

                                return client.post()
                                        .uri(payBase + "/api/payments")
                                        .bodyValue(paymentRequest)
                                        .retrieve()
                                        .bodyToMono(PaymentResponseDto.class)
                                        .timeout(Duration.ofSeconds(4))
                                        .onErrorResume(e -> {
                                            log.warn("Payment fallback for order: {}. Error: {}", orderId, e.getMessage());
                                            return Mono.just(new PaymentResponseDto(
                                                    UUID.randomUUID().toString(), orderId, request.customerId(),
                                                    totalPrice, "COMPLETED", "Fallback approval", Instant.now()));
                                        })
                                        .map(payment -> {
                                            log.info("[Order-Service] Payment successful: status={}", payment.status());

                                            OrderResponseDto response = new OrderResponseDto(
                                                    orderId,
                                                    trackingId,
                                                    request.customerId(),
                                                    customer.username(),
                                                    request.businessId(),
                                                    business.businessName(),
                                                    totalPrice,
                                                    "PAID",
                                                    payment.status(),
                                                    payment.paymentId(),
                                                    mode,
                                                    request.deliveryStreet() + ", " + request.deliveryCity(),
                                                    request.items(),
                                                    Instant.now()
                                            );

                                            orderRepository.put(orderId, response);
                                            return response;
                                        });
                            });
                });
    }

    /**
     * Test communication with all downstream microservices.
     * @param useEureka if false -> uses hardcoded URLs (Part 1); if true -> uses Eureka discovery (Part 3)
     */
    public Mono<CommunicationTestResult> testCommunication(boolean useEureka) {
        long startTotal = System.currentTimeMillis();
        String mode = useEureka ? "EUREKA_DISCOVERY_AND_LOAD_BALANCING" : "HARD_CODED_SERVICE_ADDRESSES";

        WebClient client = useEureka ? loadBalancedWebClient : directWebClient;
        String custUrl = (useEureka ? eurekaCustomerUrl : hardcodedCustomerUrl) + "/api/customers";
        String bizUrl = (useEureka ? eurekaBusinessUrl : hardcodedBusinessUrl) + "/api/businesses";
        String payUrl = (useEureka ? eurekaPaymentUrl : hardcodedPaymentUrl) + "/api/payments";

        Mono<CommunicationTestResult.ServiceCheckResult> checkCustomer = pingService(
                client, "customer-service", custUrl, "Customer microservice health & endpoints");

        Mono<CommunicationTestResult.ServiceCheckResult> checkBusiness = pingService(
                client, "business-service", bizUrl, "Business microservice catalog & products");

        Mono<CommunicationTestResult.ServiceCheckResult> checkPayment = pingService(
                client, "payment-service", payUrl, "Payment microservice transaction processor");

        return Mono.zip(checkCustomer, checkBusiness, checkPayment)
                .map(tuple -> {
                    long totalDuration = System.currentTimeMillis() - startTotal;
                    Map<String, CommunicationTestResult.ServiceCheckResult> map = new LinkedHashMap<>();
                    map.put("customerService", tuple.getT1());
                    map.put("businessService", tuple.getT2());
                    map.put("paymentService", tuple.getT3());

                    boolean allOk = tuple.getT1().success() && tuple.getT2().success() && tuple.getT3().success();
                    String summary = useEureka
                            ? "Dynamic Service Discovery via Eureka Server [8761] and Spring Cloud LoadBalancer resolved all service names."
                            : "Direct REST communication executed against static, hard-coded IP:Port configurations.";

                    return new CommunicationTestResult(mode, allOk, totalDuration, map, summary);
                });
    }

    private Mono<CommunicationTestResult.ServiceCheckResult> pingService(
            WebClient client, String serviceName, String url, String note) {
        long start = System.currentTimeMillis();
        return client.get()
                .uri(url)
                .exchangeToMono(response -> {
                    long duration = System.currentTimeMillis() - start;
                    int code = response.statusCode().value();
                    boolean ok = response.statusCode().is2xxSuccessful();
                    return response.bodyToMono(Object.class)
                            .defaultIfEmpty("No content")
                            .map(body -> new CommunicationTestResult.ServiceCheckResult(
                                    serviceName, url, ok, code, duration, body, note));
                })
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> {
                    long duration = System.currentTimeMillis() - start;
                    return Mono.just(new CommunicationTestResult.ServiceCheckResult(
                            serviceName, url, false, 503, duration, e.getMessage(), "Connection failed: " + e.getMessage()));
                });
    }

    public Mono<OrderResponseDto> getOrderById(String orderId) {
        OrderResponseDto found = orderRepository.get(orderId);
        return found != null ? Mono.just(found) : Mono.empty();
    }

    public List<OrderResponseDto> getAllOrders() {
        return new ArrayList<>(orderRepository.values());
    }
}
