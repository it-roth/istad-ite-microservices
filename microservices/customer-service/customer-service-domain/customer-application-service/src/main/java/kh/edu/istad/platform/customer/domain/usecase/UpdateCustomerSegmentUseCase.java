package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.exception.CustomerConflictException;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCustomerSegmentUseCase {
    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<CustomerSegment> update(String id, CustomerSegment changes) {
        changes.validateForUpdate();
        CustomerSegmentId segmentId = new CustomerSegmentId(UUID.fromString(id));
        return customerSegmentRepository.findById(segmentId)
                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(id)))
                .flatMap(segment -> customerSegmentRepository.findByType(changes.getType())
                        .filter(existing -> !existing.getId().equals(segment.getId()))
                        .flatMap(existing -> Mono.<CustomerSegment>error(
                                new CustomerConflictException("Customer segment type already exists")))
                        .switchIfEmpty(Mono.defer(() -> {
                            segment.updateType(changes.getType());
                            return customerSegmentRepository.save(segment);
                        })));
    }
}
