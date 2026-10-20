
package kh.edu.istad.platform.customer.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.CustomerSegmentId;
import kh.edu.istad.platform.customer.domain.entity.CustomerSegment;
import kh.edu.istad.platform.customer.persistence.entity.CustomerSegmentDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerSegmentPersistenceMapper {

    @Mapping(target = "id", expression = "java(toStringId(customerSegment.getId()))")
    CustomerSegmentDocument toDocument(CustomerSegment customerSegment);

    @Mapping(target = "id", expression = "java(toSegmentId(customerSegmentDocument.getId()))")
    CustomerSegment toDomain(CustomerSegmentDocument customerSegmentDocument);

    default String toStringId(CustomerSegmentId id) {
        return id == null ? null : id.value().toString();
    }

    default CustomerSegmentId toSegmentId(String id) {
        return id == null ? null : new CustomerSegmentId(UUID.fromString(id));
    }
}
