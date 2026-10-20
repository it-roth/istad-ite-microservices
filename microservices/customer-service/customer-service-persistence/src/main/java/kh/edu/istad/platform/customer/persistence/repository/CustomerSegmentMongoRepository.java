package kh.edu.istad.platform.customer.persistence.repository;

import kh.edu.istad.platform.customer.persistence.entity.CustomerSegmentDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerSegmentMongoRepository extends ReactiveMongoRepository<CustomerSegmentDocument,String> {

}
