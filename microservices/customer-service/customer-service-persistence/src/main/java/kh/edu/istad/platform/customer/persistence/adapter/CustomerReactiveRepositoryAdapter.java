package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilter;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistics;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerMongoRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Repository
public class CustomerReactiveRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerDocument customerDocument = customerPersistenceMapper.toDocument(customer);
        return customerMongoRepository.findById(customerDocument.getId())
                .flatMap(existing -> {
                    customerDocument.setCreatedBy(
                            existing.getCreatedBy() == null ? "SYSTEM" : existing.getCreatedBy()
                    );
                    customerDocument.setCreatedAt(
                            existing.getCreatedAt() == null ? Instant.now() : existing.getCreatedAt()
                    );
                    customerDocument.setModifiedBy("SYSTEM");
                    customerDocument.setModifiedAt(Instant.now());
                    return customerMongoRepository.save(customerDocument);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    customerDocument.setCreatedBy("SYSTEM");
                    customerDocument.setCreatedAt(Instant.now());
                    return customerMongoRepository.insert(customerDocument);
                }))
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> findAll() {
        return customerMongoRepository.findAll().map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Customer> findById(CustomerId id) {
        return customerMongoRepository
                .findById(id.value().toString())
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(CustomerId id) {
        return customerMongoRepository.deleteById(id.value().toString());
    }

    @Override
    public Mono<Customer> findByUsername(String username) {
        return customerMongoRepository.findByUsernameIgnoreCase(username)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Customer> findByFilter(CustomerFilter filter) {
        Query query = new Query();

        if (filter != null) {
            addContains(query, "username", filter.username());
            addContains(query, "familyName", filter.familyName());
            addContains(query, "givenName", filter.givenName());
            addContains(query, "email", filter.email());

            if (filter.status() != null) {
                query.addCriteria(Criteria.where("status").is(filter.status()));
            }
            if (filter.segmentId() != null && !filter.segmentId().isBlank()) {
                query.addCriteria(Criteria.where("segmentId").is(filter.segmentId()));
            }
        }

        return reactiveMongoTemplate.find(query, CustomerDocument.class)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsBySegmentId(String segmentId) {
        Query query = Query.query(Criteria.where("segmentId").is(segmentId));
        return reactiveMongoTemplate.exists(query, CustomerDocument.class);
    }

    @Override
    public Flux<CustomerStatistics> statistics() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("status", "segmentId")
                        .count()
                        .as("total")
        );

        return reactiveMongoTemplate.aggregate(
                        aggregation,
                        CustomerDocument.class,
                        Document.class
                )
                .map(result -> {
                    Document group = result.get("_id", Document.class);
                    return new CustomerStatistics(
                            CustomerStatus.valueOf(group.getString("status")),
                            group.getString("segmentId"),
                            ((Number) result.get("total")).longValue()
                    );
                });
    }

    private void addContains(Query query, String field, String value) {
        if (value != null && !value.isBlank()) {
            query.addCriteria(Criteria.where(field).regex(
                    Pattern.quote(value.trim()),
                    "i"
            ));
        }
    }
}