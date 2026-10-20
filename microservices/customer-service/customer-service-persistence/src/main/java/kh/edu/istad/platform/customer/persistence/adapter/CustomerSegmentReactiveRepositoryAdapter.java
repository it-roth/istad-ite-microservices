package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.persistence.document.CustomerSegmentDocument;
import kh.edu.istad.platform.customer.persistence.repository.CustomerSegmentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerSegmentReactiveRepositoryAdapter implements CustomerSegmentRepository {
    private final CustomerSegmentMongoRepository repository;

    @Override
    public Mono<CustomerSegment> save(CustomerSegment customerSegment) {
        return repository.save(toDocument(customerSegment)).map(this::toDomain);
    }

    @Override
    public Flux<CustomerSegment> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    @Override
    public Mono<CustomerSegment> findById(CustomerSegmentId id) {
        return repository.findById(id.value().toString()).map(this::toDomain);
    }

    @Override
    public Mono<CustomerSegment> findByType(String type) {
        return repository.findByTypeIgnoreCase(type).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(CustomerSegmentId id) {
        return repository.deleteById(id.value().toString());
    }

    private CustomerSegmentDocument toDocument(CustomerSegment customerSegment) {
        return CustomerSegmentDocument.builder()
                .id(customerSegment.getId() == null
                        ? null
                        : customerSegment.getId().value().toString())
                .type(customerSegment.getType())
                .build();
    }

    private CustomerSegment toDomain(CustomerSegmentDocument document) {
        return CustomerSegment.builder()
                .id(new CustomerSegmentId(UUID.fromString(document.getId())))
                .type(document.getType())
                .build();
    }
}
