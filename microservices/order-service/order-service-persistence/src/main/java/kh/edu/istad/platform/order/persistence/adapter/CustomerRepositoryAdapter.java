package kh.edu.istad.platform.order.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.port.out.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class CustomerRepositoryAdapter implements CustomerRepository {

    @Override
    public void findCustomer(CustomerId customerId) {
        // STUB: Assume customer always exists
        log.info("Customer validated with id: {}", customerId.value());
    }
}
