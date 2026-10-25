package kh.edu.istad.platform.order.domain.port.in;

import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;

public interface CreateOrderUseCase {
    CreateOrderResult execute(CreateOrderCommand command);
}
