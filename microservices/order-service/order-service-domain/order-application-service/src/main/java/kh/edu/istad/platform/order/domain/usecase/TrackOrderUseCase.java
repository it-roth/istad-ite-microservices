package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.platform.order.domain.dto.TrackOrderQuery;
import kh.edu.istad.platform.order.domain.dto.TrackOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrackOrderUseCase {

    private final OrderRepository orderRepository;

    public TrackOrderResult execute(TrackOrderQuery query) {
        Order order = orderRepository.findByTrackingId(new TrackingId(query.trackingId()));
        if (order == null) {
            log.warn("Could not find order with tracking id: {}", query.trackingId());
            throw new OrderDomainException("Could not find order with tracking id: " + query.trackingId());
        }
        return new TrackOrderResult(
                order.getId().value(),
                order.getOrderStatus(),
                order.getFailureMessages()
        );
    }
}
