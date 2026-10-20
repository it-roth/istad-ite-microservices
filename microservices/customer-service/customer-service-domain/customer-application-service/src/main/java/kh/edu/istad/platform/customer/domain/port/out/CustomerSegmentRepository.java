package kh.edu.istad.platform.customer.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import reactor.core.publisher.Mono;

public interface CustomerSegmentRepository {

    Mono<CustomerSegment> save(CustomerSegment customerSegment);

    Mono<CustomerSegment> findById(CustomerSegmentId customerSegmentId);
}