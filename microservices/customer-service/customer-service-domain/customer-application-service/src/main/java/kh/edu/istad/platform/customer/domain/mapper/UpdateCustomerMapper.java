package kh.edu.istad.platform.customer.domain.mapper;


import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import org.springframework.stereotype.Component;

@Component
public class UpdateCustomerMapper {
    public Customer toCustomer(UpdateCustomerCommand command) {
        return Customer.Builder.builder()
                .familyName(command.familyName())
                .givenName(command.givenName())
                .build();
    }

    public UpdateCustomerResult toResult(Customer customer) {
        return new UpdateCustomerResult(
                customer.getId().value(),
                customer.getUsername(),
                customer.getFamilyName(),
                customer.getGivenName(),
                customer.getEmail().value(),
                customer.getPhoneNumber().value()
        );
    }
}
