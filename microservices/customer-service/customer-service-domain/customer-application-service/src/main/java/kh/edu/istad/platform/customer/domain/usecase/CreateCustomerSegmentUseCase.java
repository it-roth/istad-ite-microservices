package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.exception.CustomerConflictException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CreateCustomerSegmentUseCase {
    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<CustomerSegment> create(CustomerSegment customerSegment) {
        customerSegment.initiateCustomerSegment();
        return customerSegmentRepository.findByType(customerSegment.getType())
                .flatMap(existing -> Mono.<CustomerSegment>error(
                        new CustomerConflictException("Customer segment type already exists")))
                .switchIfEmpty(Mono.defer(() -> customerSegmentRepository.save(customerSegment)));
    }
}
