package kh.edu.istad.platform.business.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.business.domain.entity.Business;

import java.time.ZonedDateTime;

public record BusinessRegisteredEvent(
        Business business,
        ZonedDateTime createdAt
) implements DomainEvent<Business> {
}
