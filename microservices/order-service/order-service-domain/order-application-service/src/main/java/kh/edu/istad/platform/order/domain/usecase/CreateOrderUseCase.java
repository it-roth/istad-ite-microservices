package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.entity.Business;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.mapper.OrderDataMapper;
import kh.edu.istad.platform.order.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.order.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderDomainService orderDomainService;
    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;

    public CreateOrderResult execute(CreateOrderCommand command) {
        checkCustomer(command.customerId());
        Business business = checkBusiness(command);
        Order order = orderDataMapper.createOrderCommandToOrder(command);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, business);
        Order savedOrder = saveOrder(order);
        log.info("Order is created with id: {}", savedOrder.getId().value());
        log.info("Order trackingId: {}", savedOrder.getTrackingId() != null ? savedOrder.getTrackingId().value() : "NULL");
        return orderDataMapper.orderToCreateOrderResult(savedOrder, "Order created successfully");
    }

    private void checkCustomer(java.util.UUID customerId) {
        customerRepository.findCustomer(new CustomerId(customerId));
        log.info("Customer with id: {} is validated", customerId);
    }

    private Business checkBusiness(CreateOrderCommand command) {
        Business business = businessRepository.findBusinessInformation(
                orderDataMapper.createOrderCommandToBusiness(command)
        );
        if (business == null) {
            log.warn("Could not find business with id: {}", command.businessId());
            throw new OrderDomainException("Could not find business with id: " + command.businessId());
        }
        return business;
    }

    private Order saveOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        if (savedOrder == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", savedOrder.getId().value());
        return savedOrder;
    }
}
