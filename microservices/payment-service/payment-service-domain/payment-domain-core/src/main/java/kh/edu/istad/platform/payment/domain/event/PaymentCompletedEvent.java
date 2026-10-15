package kh.edu.istad.platform.payment.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.payment.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.List;

public record PaymentCompletedEvent(
        Payment payment,
        ZonedDateTime createdAt,
        List<String> failureMessages
) implements DomainEvent<Payment> {
}
