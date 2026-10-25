package kh.edu.istad.platform.order.restapi.mapper;

import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.dto.TrackOrderResult;
import kh.edu.istad.platform.order.restapi.dto.CreateOrderRequest;
import kh.edu.istad.platform.order.restapi.dto.CreateOrderResponse;
import kh.edu.istad.platform.order.restapi.dto.OrderAddressRequest;
import kh.edu.istad.platform.order.restapi.dto.OrderItemRequest;
import kh.edu.istad.platform.order.restapi.dto.TrackOrderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderWebMapper {

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        List<OrderItemRequest> items = request.items();
        OrderAddressRequest address = request.address();
        
        return new CreateOrderCommand(
                request.customerId(),
                request.businessId(),
                request.price() == null
                        ? items.stream()
                                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                        : request.price(),
                items.stream()
                        .map(item -> new CreateOrderCommand.OrderItemCommand(
                                item.productId(),
                                item.quantity(),
                                item.price(),
                                item.price().multiply(BigDecimal.valueOf(item.quantity()))
                        ))
                        .collect(Collectors.toList()),
                new CreateOrderCommand.OrderAddressCommand(
                        address.street(),
                        address.postalCode(),
                        address.city()
                ),
                request.discoveryMode()
        );
    }

    public CreateOrderResponse toResponse(CreateOrderResult result) {
        return new CreateOrderResponse(
                result.orderId(),
                result.trackingId(),
                result.customerId(),
                result.customerUsername(),
                result.businessId(),
                result.businessName(),
                result.totalPrice(),
                result.orderStatus(),
                result.message(),
                result.paymentId(),
                result.paymentStatus(),
                result.communicationModeUsed(),
                result.deliveryAddress(),
                result.items().stream()
                        .map(item -> new CreateOrderResponse.OrderItemResponse(
                                item.productId(), item.quantity(), item.price(), item.subTotal()))
                        .toList(),
                result.createdAt()
        );
    }

    public TrackOrderResponse toResponse(TrackOrderResult result) {
        return new TrackOrderResponse(
                result.orderId(),
                result.orderStatus(),
                result.failureMessages()
        );
    }
}
