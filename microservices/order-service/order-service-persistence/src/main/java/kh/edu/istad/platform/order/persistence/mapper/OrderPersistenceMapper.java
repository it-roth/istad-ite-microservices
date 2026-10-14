package kh.edu.istad.platform.order.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.*;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.valueobject.OrderItemId;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;
import kh.edu.istad.platform.order.persistence.entity.OrderAddressEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().value())
                .customerId(order.getCustomerId().value())
                .businessId(order.getBusinessId().value())
                .trackingId(order.getTrackingId().value())
                .price(order.getPrice().amount())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .address(addressToAddressEntity(order.getDeliveryAddress()))
                .build();

        List<OrderItemEntity> orderItemEntities = orderItemsToOrderItemEntities(order.getItems());
        orderItemEntities.forEach(item -> item.setOrder(orderEntity));
        orderEntity.setItems(orderItemEntities);

        return orderEntity;
    }

    public Order toAggregate(OrderEntity entity) {
        return Order.builder()
                .orderId(new OrderId(entity.getId()))
                .customerId(new CustomerId(entity.getCustomerId()))
                .businessId(new BusinessId(entity.getBusinessId()))
                .deliveryAddress(addressEntityToAddress(entity.getAddress()))
                .price(new Money(entity.getPrice()))
                .items(orderItemEntitiesToOrderItems(entity.getItems()))
                .trackingId(new TrackingId(entity.getTrackingId()))
                .orderStatus(entity.getOrderStatus())
                .failureMessages(entity.getFailureMessages())
                .build();
    }

    private OrderAddressEntity addressToAddressEntity(StreetAddress address) {
        return OrderAddressEntity.builder()
                .street(address.street())
                .postalCode(address.postalCode())
                .city(address.city())
                .build();
    }

    private StreetAddress addressEntityToAddress(OrderAddressEntity entity) {
        return new StreetAddress(
                entity.getStreet(),
                entity.getPostalCode(),
                entity.getCity()
        );
    }

    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(item -> OrderItemEntity.builder()
                        .id(item.getId().value())
                        .productId(item.getProduct().getId().value())
                        .price(item.getPrice().amount())
                        .quantity(item.getQuantity())
                        .subTotal(item.getSubTotal().amount())
                        .build())
                .collect(Collectors.toList());
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> entities) {
        return entities.stream()
                .map(entity -> OrderItem.builder()
                        .orderItemId(new OrderItemId(entity.getId()))
                        .product(Product.builder()
                                .id(new ProductId(entity.getProductId()))
                                .price(new Money(entity.getPrice()))
                                .build())
                        .quantity(entity.getQuantity())
                        .price(new Money(entity.getPrice()))
                        .subTotal(new Money(entity.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }
}
