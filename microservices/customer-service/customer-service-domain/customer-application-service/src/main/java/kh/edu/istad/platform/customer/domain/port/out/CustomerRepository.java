package kh.edu.istad.platform.customer.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;

public interface CustomerRepository {

    Customer save(Customer customer);

    Customer findById(CustomerId customerId);

}
