package kh.edu.istad.platform.customer.persistence.repository;

import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerMongoRepository extends ReactiveMongoRepository<CustomerDocument, String> {

    Mono<CustomerDocument> findByUsernameIgnoreCase(String username);
}
