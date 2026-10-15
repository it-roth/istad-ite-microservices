package kh.edu.istad.platform.business.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.business.domain.entity.OrderApproval;

import java.time.ZonedDateTime;

public record OrderApprovedEvent(
        OrderApproval orderApproval,
        ZonedDateTime createdAt
) implements DomainEvent<OrderApproval> {
}
