package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.entity.Role;
import kh.edu.istad.platform.iam.domain.valueobject.RoleId;
import kh.edu.istad.platform.iam.persistence.entity.RoleJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RolePersistenceMapper {

    @Mapping(source = "id.value", target = "uuid")
    @Mapping(source = "internalId", target = "id")
    @Mapping(source = "system", target = "isSystem")
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "deleted", target = "isDeleted")
    RoleJpaEntity toEntity(Role role);

    @Mapping(source = "uuid", target = "id", qualifiedByName = "uuidToRoleId")
    @Mapping(source = "id", target = "internalId")
    @Mapping(source = "isSystem", target = "system")
    @Mapping(source = "isActive", target = "active")
    @Mapping(source = "isDeleted", target = "deleted")
    Role toDomain(RoleJpaEntity entity);

    @Named("uuidToRoleId")
    default RoleId uuidToRoleId(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return new RoleId(uuid);
    }
}

