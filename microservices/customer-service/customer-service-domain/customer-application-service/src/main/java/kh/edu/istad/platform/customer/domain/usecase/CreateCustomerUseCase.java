package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.exception.CustomerConflictException;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<Customer> create(Customer customer) {
        Mono<Void> validateSegment = customer.getSegmentId() == null
                ? Mono.empty()
                : customerSegmentRepository.findById(customer.getSegmentId())
                        .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                                customer.getSegmentId().value().toString()
                        )))
                        .then();

        customer.initiateCustomer();
        return validateSegment.then(customerRepository.findByUsername(customer.getUsername()))
                .flatMap(existing -> Mono.<Customer>error(
                        new CustomerConflictException("Customer username is already in use")))
                .switchIfEmpty(Mono.defer(() -> customerRepository.save(customer)));
    }
}
