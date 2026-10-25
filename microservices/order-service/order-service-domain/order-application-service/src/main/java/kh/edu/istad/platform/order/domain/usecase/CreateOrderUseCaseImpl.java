package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.dto.CustomerServiceResponse;
import kh.edu.istad.platform.order.domain.dto.PaymentCommand;
import kh.edu.istad.platform.order.domain.dto.PaymentServiceResponse;
import kh.edu.istad.platform.order.domain.entity.Business;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.mapper.OrderDataMapper;
import kh.edu.istad.platform.order.domain.port.in.CreateOrderUseCase;
import kh.edu.istad.platform.order.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.order.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;

import java.util.logging.Logger;

public class CreateOrderUseCaseImpl implements CreateOrderUseCase {
    private static final Logger LOGGER = Logger.getLogger(CreateOrderUseCaseImpl.class.getName());
    private final OrderDomainService orderDomainService;
    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    public CreateOrderUseCaseImpl(
            OrderDomainService orderDomainService,
            OrderDataMapper orderDataMapper,
            OrderRepository orderRepository,
            BusinessRepository businessRepository,
            CustomerRepository customerRepository,
            PaymentRepository paymentRepository
    ) {
        this.orderDomainService = orderDomainService;
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
        this.businessRepository = businessRepository;
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public CreateOrderResult execute(CreateOrderCommand command) {
        CustomerServiceResponse customer = checkCustomer(command);
        Business business = checkBusiness(command);
        Order order = orderDataMapper.createOrderCommandToOrder(command);
        orderDomainService.validateAndInitiateOrder(order, business);
        PaymentServiceResponse payment = paymentRepository.processPayment(
                new PaymentCommand(
                        order.getId().value(),
                        command.customerId(),
                        order.getPrice().amount(),
                        "ISTAD_PAY"),
                discoveryMode(command));
        validatePayment(payment, order);
        orderDomainService.payOrder(order);
        Order savedOrder = saveOrder(order);
        LOGGER.info(() -> "Order is created with id: " + savedOrder.getId().value());
        return orderDataMapper.orderToCreateOrderResult(
                savedOrder,
                "Order created successfully",
                payment.paymentId(),
                payment.status(),
                discoveryMode(command),
                customer.username(),
                business.getBusinessName());
    }

    private CustomerServiceResponse checkCustomer(CreateOrderCommand command) {
        CustomerServiceResponse customer = customerRepository.findCustomer(
                new CustomerId(command.customerId()),
                discoveryMode(command));
        if (customer == null || customer.id() == null
                || !command.customerId().equals(customer.id().value())) {
            throw new OrderDomainException("Customer service returned no matching customer");
        }
        LOGGER.info(() -> "Customer with id: " + command.customerId() + " is validated");
        return customer;
    }

    private Business checkBusiness(CreateOrderCommand command) {
        Business business = businessRepository.findBusinessInformation(
                orderDataMapper.createOrderCommandToBusiness(command),
                discoveryMode(command)
        );
        if (business == null) {
            LOGGER.warning("Could not find business with id: " + command.businessId());
            throw new OrderDomainException("Could not find business with id: " + command.businessId());
        }
        return business;
    }

    private void validatePayment(PaymentServiceResponse payment, Order order) {
        if (payment == null
                || payment.paymentId() == null
                || !order.getId().value().equals(payment.orderId())
                || !order.getCustomerId().value().equals(payment.customerId())
                || payment.price() == null
                || order.getPrice().amount().compareTo(payment.price()) != 0
                || !"COMPLETED".equalsIgnoreCase(payment.status())) {
            throw new OrderDomainException("Payment service did not confirm this order");
        }
    }

    private String discoveryMode(CreateOrderCommand command) {
        return "HARD_CODED".equalsIgnoreCase(command.discoveryMode())
                ? "HARD_CODED"
                : "EUREKA_DISCOVERY";
    }

    private Order saveOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        if (savedOrder == null) {
            LOGGER.severe("Could not save order");
            throw new OrderDomainException("Could not save order!");
        }
        LOGGER.info(() -> "Order is saved with id: " + savedOrder.getId().value());
        return savedOrder;
    }
}
