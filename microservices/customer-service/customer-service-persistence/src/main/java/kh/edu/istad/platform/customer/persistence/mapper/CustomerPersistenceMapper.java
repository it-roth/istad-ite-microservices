package kh.edu.istad.platform.customer.persistence.mapper;

import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerPersistenceMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "phoneNumber.value", target = "phoneNumber")
    @Mapping(target = "segmentId", expression = "java(customer.getSegmentId() == null ? null : customer.getSegmentId().value().toString())")
    CustomerDocument toDocument(Customer customer);

    @Mapping(source = "id", target = "id.value")
    @Mapping(source = "email", target = "email.value")
    @Mapping(source = "phoneNumber", target = "phoneNumber.value")
    @Mapping(target = "segmentId", expression = "java(toSegmentId(customerDocument.getSegmentId()))")
    Customer toDomain(CustomerDocument customerDocument);

    default kh.edu.istad.common.domain.valueobject.CustomerSegmentId toSegmentId(String value) {
        return value == null ? null : new kh.edu.istad.common.domain.valueobject.CustomerSegmentId(
                java.util.UUID.fromString(value)
        );
    }

    default java.util.UUID toUUID(String value) {
        return value == null ? null : java.util.UUID.fromString(value);
    }
}
