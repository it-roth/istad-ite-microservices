package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.exception.CustomerConflictException;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCustomerSegmentUseCase {
    private final CustomerSegmentRepository customerSegmentRepository;
    private final CustomerRepository customerRepository;

    public Mono<Void> delete(String id) {
        CustomerSegmentId segmentId = new CustomerSegmentId(UUID.fromString(id));
        return customerSegmentRepository.findById(segmentId)
                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(id)))
                .flatMap(segment -> customerRepository.existsBySegmentId(id)
                        .flatMap(inUse -> inUse
                                ? Mono.error(new CustomerConflictException(
                                        "Customer segment is assigned to customers"))
                                : customerSegmentRepository.deleteById(segmentId)));
    }
}
