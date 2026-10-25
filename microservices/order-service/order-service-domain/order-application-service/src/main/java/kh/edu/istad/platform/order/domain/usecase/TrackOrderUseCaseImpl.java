package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.platform.order.domain.dto.TrackOrderQuery;
import kh.edu.istad.platform.order.domain.dto.TrackOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.in.TrackOrderUseCase;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;

import java.util.logging.Logger;

public class TrackOrderUseCaseImpl implements TrackOrderUseCase {
    private static final Logger LOGGER = Logger.getLogger(TrackOrderUseCaseImpl.class.getName());
    private final OrderRepository orderRepository;

    public TrackOrderUseCaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public TrackOrderResult execute(TrackOrderQuery query) {
        Order order = orderRepository.findByTrackingId(new TrackingId(query.trackingId()));
        if (order == null) {
            LOGGER.warning("Could not find order with tracking id: " + query.trackingId());
            throw new OrderDomainException("Could not find order with tracking id: " + query.trackingId());
        }
        return new TrackOrderResult(
                order.getId().value(),
                order.getOrderStatus(),
                order.getFailureMessages()
        );
    }
}
