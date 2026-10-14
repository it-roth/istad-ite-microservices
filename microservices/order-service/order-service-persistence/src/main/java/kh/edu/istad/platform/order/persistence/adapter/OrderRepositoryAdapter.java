package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;
import kh.edu.istad.platform.order.persistence.mapper.OrderPersistenceMapper;
import kh.edu.istad.platform.order.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderPersistenceMapper orderPersistenceMapper;

    @Override
    public Order save(Order order) {
        var orderEntity = orderPersistenceMapper.toEntity(order);
        var savedEntity = orderJpaRepository.save(orderEntity);
        log.info("Order saved with id: {}", savedEntity.getId());
        return order;
    }

    @Override
    public Order findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.value())
                .map(orderPersistenceMapper::toAggregate)
                .orElse(null);
    }
}
