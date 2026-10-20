package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerSegmentPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerSegmentMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomerSegmentReactiveMongoRepositoryAdapter implements CustomerSegmentRepository {

    private final CustomerSegmentMongoRepository customerSegmentMongoRepository;
    private final CustomerSegmentPersistenceMapper customerSegmentPersistenceMapper;
    @Override
    public Mono<CustomerSegment> save(CustomerSegment customerSegment) {
        return customerSegmentMongoRepository.save(customerSegmentPersistenceMapper.toDocument(customerSegment)).map(customerSegmentPersistenceMapper::toDomain);
    }

    @Override
    public Mono<CustomerSegment> findById(CustomerSegmentId customerSegmentId) {
        return customerSegmentMongoRepository
                .findById(customerSegmentId.value().toString()).map(customerSegmentPersistenceMapper::toDomain);
    }
}
