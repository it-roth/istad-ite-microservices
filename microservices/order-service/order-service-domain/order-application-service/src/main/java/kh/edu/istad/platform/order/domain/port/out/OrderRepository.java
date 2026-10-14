package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;

public interface OrderRepository {
    Order save(Order order);
    Order findByTrackingId(TrackingId trackingId);
}
