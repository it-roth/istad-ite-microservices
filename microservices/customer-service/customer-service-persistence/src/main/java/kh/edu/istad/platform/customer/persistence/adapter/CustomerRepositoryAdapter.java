package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.persistence.entity.CustomerEntity;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;

    @Override
    public Customer save(Customer customer) {

        log.info("Customer domain ID: {}", customer.getId());

        CustomerEntity entity = customerPersistenceMapper.toEntity(customer);

        log.info("Customer entity ID: {}", entity.getId());

        customerJpaRepository.save(entity);

        return customer;
    }

    @Override
    public Customer findById(CustomerId customerId) {
        return customerJpaRepository.findById(customerId.value()).map(customerPersistenceMapper::toAggregate).orElse(null);
    }
}