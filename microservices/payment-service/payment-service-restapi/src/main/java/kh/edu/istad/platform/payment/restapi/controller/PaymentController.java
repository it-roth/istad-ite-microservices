package kh.edu.istad.platform.payment.restapi.controller;

import kh.edu.istad.platform.payment.restapi.dto.PaymentRequest;
import kh.edu.istad.platform.payment.restapi.dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    private final Map<String, PaymentResponse> paymentsByOrderId = new ConcurrentHashMap<>();
    private final Map<String, PaymentResponse> paymentsById = new ConcurrentHashMap<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        log.info("Processing payment for Order: {}, Customer: {}, Amount: {}",
                request.orderId(), request.customerId(), request.price());

        String paymentId = UUID.randomUUID().toString();
        PaymentResponse response = new PaymentResponse(
                paymentId,
                request.orderId(),
                request.customerId(),
                request.price(),
                "COMPLETED",
                "Payment successfully verified and settled via ISTAD Pay",
                Instant.now()
        );

        paymentsById.put(paymentId, response);
        if (request.orderId() != null) {
            paymentsByOrderId.put(request.orderId(), response);
        }

        log.info("Payment approved. PaymentId: {}", paymentId);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("id") String id) {
        PaymentResponse response = paymentsById.get(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable("orderId") String orderId) {
        PaymentResponse response = paymentsByOrderId.get(orderId);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return new ArrayList<>(paymentsById.values());
    }
}
