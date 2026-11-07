package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.entity.Permission;
import kh.edu.istad.platform.iam.domain.valueobject.PermissionId;
import kh.edu.istad.platform.iam.persistence.entity.PermissionJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissionPersistenceMapper {

    @Mapping(source = "id.value", target = "uuid")
    @Mapping(source = "internalId", target = "id")
    @Mapping(source = "system", target = "isSystem")
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "deleted", target = "isDeleted")
    PermissionJpaEntity toEntity(Permission permission);

    @Mapping(source = "uuid", target = "id", qualifiedByName = "uuidToPermissionId")
    @Mapping(source = "id", target = "internalId")
    @Mapping(source = "isSystem", target = "system")
    @Mapping(source = "isActive", target = "active")
    @Mapping(source = "isDeleted", target = "deleted")
    Permission toDomain(PermissionJpaEntity entity);

    @Named("uuidToPermissionId")
    default PermissionId uuidToPermissionId(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return new PermissionId(uuid);
    }
}

