package kh.edu.istad.platform.customer.domain.mapper;

import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiatedCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerDomainMapper {

    public Customer fromInitiateCustomerCommandToCustomer(
            InitiateCustomerCommand command) {
        return Customer.Builder.builder()
                .username(command.username())
                .familyName(command.familyName())
                .givenName(command.givenName())
                .email(new Email(command.email()))
                .phoneNumber(new PhoneNumber(command.phoneNumber()))
                .customerSegmentId(
                        new CustomerSegmentId(command.customerSegmentId())
                )
                .build();
    }

    public InitiatedCustomerResult fromCustomerToInitiatedCustomerResult(
            Customer customer,
            String customerSegmentType) {
        return new InitiatedCustomerResult(
                customer.getId().value(),
                customer.getUsername(),
                customer.getFamilyName(),
                customer.getGivenName(),
                customer.getEmail().value(),
                customer.getPhoneNumber().value(),
                customerSegmentType
        );
    }

    public Customer fromUpdatedCustomerCommandToCustomer(
            UpdateCustomerCommand command) {
        return Customer.Builder.builder()
                .familyName(command.familyName())
                .givenName(command.givenName())
                .build();
    }

    public UpdateCustomerResult fromCustomerToUpdateCustomerResult(
            Customer customer) {
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