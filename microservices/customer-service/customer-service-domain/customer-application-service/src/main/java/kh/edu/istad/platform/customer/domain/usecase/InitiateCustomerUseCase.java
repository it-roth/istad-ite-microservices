package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.mapper.InitiateCustomerMapper;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitiateCustomerUseCase {

    private final InitiateCustomerMapper customerDomainMapper;
    private final CustomerRepository customerRepository;

    public InitiateCustomerResult execute(InitiateCustomerCommand command) {
        validate(command);
        Customer customer = customerDomainMapper.fromInitiateCustomerCommandToCustomer(command);

        log.info("Before initiate: {}", customer.getId());

        customer.initiateCustomer();

        log.info("After initiate: {}", customer.getId());

        Customer savedCustomer = customerRepository.save(customer);
        return customerDomainMapper.fromCustomerToInitiatedCustomerResult(savedCustomer != null ? savedCustomer : customer);
    }

    private void validate(InitiateCustomerCommand command) {

        if (command.username() == null || command.username().isBlank()) {
            throw new CustomerDomainException("Username must not be null or blank");
        }

        if (command.email() == null || command.email().isBlank()) {
            throw new CustomerDomainException("Email must not be null or blank");
        }

        if (command.phoneNumber() == null || command.phoneNumber().isBlank()) {
            throw new CustomerDomainException("Phone number must not be null or blank");
        }
    }
}