package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;

public interface CustomerRepository {
    void findCustomer(CustomerId customerId);
}
