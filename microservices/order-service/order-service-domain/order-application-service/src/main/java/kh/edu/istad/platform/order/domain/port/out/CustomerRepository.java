package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.order.domain.dto.CustomerServiceResponse;

public interface CustomerRepository {
    CustomerServiceResponse findCustomer(CustomerId customerId, String discoveryMode);
}
