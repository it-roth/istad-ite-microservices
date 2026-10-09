package kh.edu.istad.platform.customer.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import kh.edu.istad.platform.customer.persistence.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CustomerPersistenceMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "customerIdToUuid")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "phoneNumberToString")
    CustomerEntity toEntity(Customer customer);

    default Customer toAggregate(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }

        return Customer.builder() // <-- Changed from Customer.Builder.builder()
                .id(new CustomerId(entity.getId()))
                .username(entity.getUsername())
                .familyName(entity.getFamilyName())
                .givenName(entity.getGivenName())
                .email(new Email(entity.getEmail()))
                .phoneNumber(new PhoneNumber(entity.getPhoneNumber()))
                .status(entity.getStatus())
                .build();
    }

    @Named("customerIdToUuid")
    default UUID customerIdToUuid(CustomerId id) {
        return id == null ? null : id.value();
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email == null ? null : email.value();
    }

    @Named("phoneNumberToString")
    default String phoneNumberToString(PhoneNumber phoneNumber) {
        return phoneNumber == null ? null : phoneNumber.value();
    }
}