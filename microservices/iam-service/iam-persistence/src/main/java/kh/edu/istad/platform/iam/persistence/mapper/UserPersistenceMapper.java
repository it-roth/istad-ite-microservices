package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.entity.User;
import kh.edu.istad.platform.iam.domain.valueobject.UserId;
import kh.edu.istad.platform.iam.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserPersistenceMapper {

    @Mapping(source = "id.value", target = "uuid")
    @Mapping(source = "internalId", target = "id")
    @Mapping(source = "deleted", target = "isDeleted")
    UserJpaEntity toEntity(User user);

    @Mapping(source = "uuid", target = "id", qualifiedByName = "uuidToUserId")
    @Mapping(source = "id", target = "internalId")
    @Mapping(source = "isDeleted", target = "deleted")
    User toDomain(UserJpaEntity entity);

    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return new UserId(uuid);
    }
}

