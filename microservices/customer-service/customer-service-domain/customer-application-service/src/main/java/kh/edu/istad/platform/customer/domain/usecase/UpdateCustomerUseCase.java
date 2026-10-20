package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerResult;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.mapper.UpdateCustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final UpdateCustomerMapper updateCustomerMapper;

    public Mono<UpdateCustomerResult> execute(
            CustomerId id,
            UpdateCustomerCommand command
    ) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.updateCustomer(command.familyName(), command.givenName());
                    return customer;
                })
                .flatMap(customerRepository::save)
                .map(updateCustomerMapper::toResult);
    }
}