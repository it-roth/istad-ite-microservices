package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryCustomerSegmentUseCase {
    private final CustomerSegmentRepository customerSegmentRepository;

    public Flux<CustomerSegment> findAll() {
        return customerSegmentRepository.findAll();
    }

    public Mono<CustomerSegment> findById(String id) {
        CustomerSegmentId segmentId = new CustomerSegmentId(UUID.fromString(id));
        return customerSegmentRepository.findById(segmentId)
                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(id)));
    }
}
