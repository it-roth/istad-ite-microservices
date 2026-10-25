package kh.edu.istad.platform.order.domain.mapper;

import kh.edu.istad.common.domain.valueobject.*;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.entity.Business;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderDataMapper {

    public Order createOrderCommandToOrder(CreateOrderCommand command) {
        return Order.builder()
                .customerId(new CustomerId(command.customerId()))
                .businessId(new BusinessId(command.businessId()))
                .deliveryAddress(new StreetAddress(
                        command.address().street(),
                        command.address().postalCode(),
                        command.address().city()
                ))
                .price(new Money(command.price()))
                .items(orderItemsCommandToOrderItems(command.items()))
                .build();
    }

    public CreateOrderResult orderToCreateOrderResult(
            Order order,
            String message,
            UUID paymentId,
            String paymentStatus,
            String communicationModeUsed,
            String customerUsername,
            String businessName
    ) {
        return new CreateOrderResult(
                order.getId().value(),
                order.getTrackingId().value(),
                order.getCustomerId().value(),
                customerUsername,
                order.getBusinessId().value(),
                businessName,
                order.getPrice().amount(),
                order.getOrderStatus(),
                message,
                paymentId,
                paymentStatus,
                communicationModeUsed,
                order.getDeliveryAddress().street() + ", " + order.getDeliveryAddress().city(),
                order.getItems().stream()
                        .map(item -> new CreateOrderResult.OrderItemResult(
                                item.getProduct().getId().value(),
                                item.getQuantity(),
                                item.getPrice().amount(),
                                item.getSubTotal().amount()))
                        .toList(),
                Instant.now()
        );
    }

    public Business createOrderCommandToBusiness(CreateOrderCommand command) {
        return Business.builder()
                .businessId(new BusinessId(command.businessId()))
                .products(command.items().stream()
                        .map(item -> Product.builder()
                                .id(new ProductId(item.productId()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private List<OrderItem> orderItemsCommandToOrderItems(
            List<CreateOrderCommand.OrderItemCommand> items) {
        return items.stream()
                .map(item -> OrderItem.builder()
                        .product(Product.builder()
                                .id(new ProductId(item.productId()))
                                .price(new Money(item.price()))
                                .build())
                        .quantity(item.quantity())
                        .price(new Money(item.price()))
                        .subTotal(new Money(item.subTotal()))
                        .build())
                .collect(Collectors.toList());
    }
}
