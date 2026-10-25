package kh.edu.istad.platform.order.domain.port.in;

import kh.edu.istad.platform.order.domain.dto.TrackOrderQuery;
import kh.edu.istad.platform.order.domain.dto.TrackOrderResult;

public interface TrackOrderUseCase {
    TrackOrderResult execute(TrackOrderQuery query);
}
