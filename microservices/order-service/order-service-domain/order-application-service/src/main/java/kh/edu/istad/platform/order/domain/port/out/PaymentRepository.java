package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.platform.order.domain.dto.PaymentCommand;
import kh.edu.istad.platform.order.domain.dto.PaymentServiceResponse;

public interface PaymentRepository {
    PaymentServiceResponse processPayment(PaymentCommand command, String discoveryMode);
}
