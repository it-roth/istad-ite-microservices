package kh.edu.istad.platform.order.controller;

import kh.edu.istad.platform.order.dto.CommunicationTestResult;
import kh.edu.istad.platform.order.dto.CreateOrderRequest;
import kh.edu.istad.platform.order.dto.OrderResponseDto;
import kh.edu.istad.platform.order.service.OrderOrchestratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderOrchestratorService orderOrchestratorService;

    public OrderController(OrderOrchestratorService orderOrchestratorService) {
        this.orderOrchestratorService = orderOrchestratorService;
    }

    /**
     * Create order endpoint orchestrating calls across Customer, Business, and Payment services.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderResponseDto> createOrder(@RequestBody CreateOrderRequest request) {
        return orderOrchestratorService.createOrder(request);
    }

    /**
     * Retrieve order by ID.
     */
    @GetMapping("/{id}")
    public Mono<OrderResponseDto> getOrderById(@PathVariable("id") String id) {
        return orderOrchestratorService.getOrderById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id)));
    }

    /**
     * List all processed orders.
     */
    @GetMapping
    public Flux<OrderResponseDto> getAllOrders() {
        return Flux.fromIterable(orderOrchestratorService.getAllOrders());
    }

    @GetMapping("/communication-test/hard-coded")
    public Mono<CommunicationTestResult> testHardCodedCommunication() {
        log.info("[Part 1 Test] Testing communication with hard-coded addresses");
        return orderOrchestratorService.testCommunication(false);
    }


    @GetMapping("/communication-test/eureka")
    public Mono<CommunicationTestResult> testEurekaCommunication() {
        log.info("[Part 3 Test] Testing communication with Eureka service discovery & Load Balancing");
        return orderOrchestratorService.testCommunication(true);
    }
}
