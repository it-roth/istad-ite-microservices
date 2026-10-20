package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.exception.CustomerConflictException;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.exception.CustomerSegmentNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.port.out.CustomerSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCustomerUseCase {
    private final CustomerRepository customerRepository;
    private final CustomerSegmentRepository customerSegmentRepository;

    public Mono<Customer> update(String id, Customer changes) {
        changes.validateForUpdate();
        CustomerId customerId = new CustomerId(UUID.fromString(id));
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(customer -> ensureUsernameAvailable(customer, changes.getUsername())
                        .then(validateSegment(changes))
                        .then(Mono.defer(() -> {
                            customer.updateCustomer(
                                    changes.getUsername(),
                                    changes.getFamilyName(),
                                    changes.getGivenName(),
                                    changes.getEmail(),
                                    changes.getPhoneNumber(),
                                    changes.getSegmentId()
                            );
                            return customerRepository.save(customer);
                        })));
    }

    private Mono<Void> ensureUsernameAvailable(Customer current, String username) {
        return customerRepository.findByUsername(username)
                .filter(existing -> !existing.getId().equals(current.getId()))
                .flatMap(existing -> Mono.<Void>error(
                        new CustomerConflictException("Customer username is already in use")))
                .then();
    }

    private Mono<Void> validateSegment(Customer customer) {
        if (customer.getSegmentId() == null) {
            return Mono.empty();
        }
        return customerSegmentRepository.findById(customer.getSegmentId())
                .switchIfEmpty(Mono.error(new CustomerSegmentNotFoundException(
                        customer.getSegmentId().value().toString()
                )))
                .then();
    }
}
