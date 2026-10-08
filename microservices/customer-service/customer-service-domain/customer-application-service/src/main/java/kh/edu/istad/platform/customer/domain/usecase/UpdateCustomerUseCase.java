package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.mapper.UpdateCustomerMapper;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateCustomerUseCase {
    private final CustomerRepository customerRepository;
    private final UpdateCustomerMapper updateCustomerMapper;

    public UpdateCustomerResult execute(CustomerId id, UpdateCustomerCommand updateCustomerCommand){
        Customer customer = customerRepository.findById(id);
        customer.updateCustomer(
                updateCustomerCommand.familyName(),
                updateCustomerCommand.givenName()
        );
        customerRepository.save(customer);
        return updateCustomerMapper.toResult(customer);
    }
}
