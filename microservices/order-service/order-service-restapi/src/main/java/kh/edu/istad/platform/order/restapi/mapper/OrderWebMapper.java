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
                request.price(),
                items.stream()
                        .map(item -> new CreateOrderCommand.OrderItemCommand(
                                item.productId(),
                                item.quantity(),
                                item.price(),
                                item.subTotal()
                        ))
                        .collect(Collectors.toList()),
                new CreateOrderCommand.OrderAddressCommand(
                        address.street(),
                        address.postalCode(),
                        address.city()
                )
        );
    }

    public CreateOrderResponse toResponse(CreateOrderResult result) {
        return new CreateOrderResponse(
                result.orderId(),
                result.trackingId(),
                result.orderStatus(),
                result.message()
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
