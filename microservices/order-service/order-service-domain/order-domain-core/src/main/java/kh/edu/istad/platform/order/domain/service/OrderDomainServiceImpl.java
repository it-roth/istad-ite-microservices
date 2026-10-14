package kh.edu.istad.platform.order.domain.service;

import kh.edu.istad.platform.order.domain.entity.Business;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.event.OrderCancelledEvent;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.event.OrderPaidEvent;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Business business) {
        validateBusiness(business);
        setOrderProductInformation(order, business);
        order.validateOrder();
        order.initializeOrder();
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
    }

    private void validateBusiness(Business business) {
        if (!business.isActive()) {
            throw new OrderDomainException("Business with id " + business.getId().value() +
                    " is currently not active!");
        }
    }

    private void setOrderProductInformation(Order order, Business business) {
        order.getItems().forEach(orderItem ->
                business.getProducts().forEach(businessProduct -> {
                    Product currentProduct = orderItem.getProduct();
                    if (currentProduct.getId().equals(businessProduct.getId())) {
                        currentProduct.updateWithConfirmedNameAndPrice(
                                businessProduct.getName(),
                                businessProduct.getPrice()
                        );
                    }
                })
        );
    }
}
