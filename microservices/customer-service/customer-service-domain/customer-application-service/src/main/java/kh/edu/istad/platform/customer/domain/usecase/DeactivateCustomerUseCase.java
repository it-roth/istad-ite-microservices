package kh.edu.istad.platform.customer.domain.usecase;


import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.mapper.InitiateCustomerMapper;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeactivateCustomerUseCase {

    private final CustomerRepository customerRepository;

    public void execute(String id){
        CustomerId customerId = new CustomerId(UUID.fromString(id));
        Customer customer = customerRepository.findById(customerId);
        customer.deactivateCustomer();
        customerRepository.save(customer);
        log.info("Customer deactivated : {}",customerId);
    }
}
