package kh.edu.istad.platform.order.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.order.domain.dto.TrackOrderQuery;
import kh.edu.istad.platform.order.domain.port.in.CreateOrderUseCase;
import kh.edu.istad.platform.order.domain.port.in.TrackOrderUseCase;
import kh.edu.istad.platform.order.restapi.dto.CreateOrderRequest;
import kh.edu.istad.platform.order.restapi.dto.CreateOrderResponse;
import kh.edu.istad.platform.order.restapi.dto.TrackOrderResponse;
import kh.edu.istad.platform.order.restapi.mapper.OrderWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final TrackOrderUseCase trackOrderUseCase;
    private final OrderWebMapper orderWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for customer: {} at business: {}",
                request.customerId(), request.businessId());

        var command = orderWebMapper.toCommand(request);
        var result = createOrderUseCase.execute(command);

        log.info("Order created with id: {}", result.orderId());
        return orderWebMapper.toResponse(result);
    }

    @GetMapping("/{trackingId}")
    public TrackOrderResponse trackOrder(@PathVariable("trackingId") UUID trackingId) {
        log.info("Tracking order with tracking id: {}", trackingId);

        var query = new TrackOrderQuery(trackingId);
        var result = trackOrderUseCase.execute(query);

        return orderWebMapper.toResponse(result);
    }
}
