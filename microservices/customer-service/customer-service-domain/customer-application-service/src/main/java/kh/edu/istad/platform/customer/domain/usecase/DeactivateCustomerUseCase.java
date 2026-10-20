package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeactivateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Mono<Void> execute(CustomerId customerId) {
        return customerRepository.findById(customerId)
                .map(customer -> {
                    customer.deactivateCustomer();
                    return customer;
                })
                .flatMap(customerRepository::save)
                .doOnSuccess(saved ->
                        log.info("Customer deactivated: {}", customerId))
                .then();
    }
}