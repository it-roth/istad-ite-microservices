package kh.edu.istad.platform.business.persistence.repository;

import kh.edu.istad.platform.business.persistence.document.BusinessDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BusinessReactiveMongoRepository extends ReactiveMongoRepository<BusinessDocument, String> {
}
