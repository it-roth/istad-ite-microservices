package kh.edu.istad.platform.order.bean;

import kh.edu.istad.platform.order.domain.mapper.OrderDataMapper;
import kh.edu.istad.platform.order.domain.port.in.CreateOrderUseCase;
import kh.edu.istad.platform.order.domain.port.in.TestOrderCommunicationUseCase;
import kh.edu.istad.platform.order.domain.port.in.TrackOrderUseCase;
import kh.edu.istad.platform.order.domain.port.out.BusinessRepository;
import kh.edu.istad.platform.order.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.port.out.OrderServiceGateway;
import kh.edu.istad.platform.order.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.service.OrderDomainServiceImpl;
import kh.edu.istad.platform.order.domain.usecase.CreateOrderUseCaseImpl;
import kh.edu.istad.platform.order.domain.usecase.TestOrderCommunicationUseCaseImpl;
import kh.edu.istad.platform.order.domain.usecase.TrackOrderUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

    @Bean
    public OrderDataMapper orderDataMapper() {
        return new OrderDataMapper();
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderDomainService orderDomainService,
            OrderDataMapper orderDataMapper,
            OrderRepository orderRepository,
            BusinessRepository businessRepository,
            CustomerRepository customerRepository,
            PaymentRepository paymentRepository
    ) {
        return new CreateOrderUseCaseImpl(
                orderDomainService,
                orderDataMapper,
                orderRepository,
                businessRepository,
                customerRepository,
                paymentRepository
        );
    }

    @Bean
    public TrackOrderUseCase trackOrderUseCase(OrderRepository orderRepository) {
        return new TrackOrderUseCaseImpl(orderRepository);
    }

    @Bean
    public TestOrderCommunicationUseCase testOrderCommunicationUseCase(
            OrderServiceGateway orderServiceGateway
    ) {
        return new TestOrderCommunicationUseCaseImpl(orderServiceGateway);
    }
}
