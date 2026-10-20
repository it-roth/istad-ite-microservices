package kh.edu.istad.platform.customer.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;

import java.time.ZonedDateTime;


public class CustomerSegmentInitiatedEvent implements DomainEvent<CustomerSegment> {
    private final CustomerSegment customerSegment;
    private final ZonedDateTime zonedDateTime;

    public CustomerSegment getCustomerSegment() {
        return customerSegment;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public CustomerSegmentInitiatedEvent(CustomerSegment customerSegment, ZonedDateTime zonedDateTime) {
        this.customerSegment = customerSegment;
        this.zonedDateTime = zonedDateTime;
    }
}
