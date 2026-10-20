package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilter;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistics;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class QueryCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Flux<Customer> findAll(){
        return customerRepository.findAll();
    }

    public Flux<Customer> findByFilter(CustomerFilter filter) {
        return customerRepository.findByFilter(filter);
    }

    public Mono<Customer> findById(String id) {
        CustomerId customerId = new CustomerId(java.util.UUID.fromString(id));
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)));
    }

    public Flux<CustomerStatistics> statistics() {
        return customerRepository.statistics();
    }
}
