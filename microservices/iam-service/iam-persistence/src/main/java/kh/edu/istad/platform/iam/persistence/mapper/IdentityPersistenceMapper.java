package kh.edu.istad.platform.iam.persistence.mapper;

import kh.edu.istad.platform.iam.domain.entity.Identity;
import kh.edu.istad.platform.iam.domain.valueobject.IdentityId;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;
import kh.edu.istad.platform.iam.persistence.entity.IdentityJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IdentityPersistenceMapper {

    @Mapping(source = "id.value", target = "uuid")
    @Mapping(source = "internalId", target = "id")
    @Mapping(source = "provider", target = "provider", qualifiedByName = "providerToString")
    @Mapping(source = "deleted", target = "isDeleted")
    IdentityJpaEntity toEntity(Identity identity);

    @Mapping(source = "uuid", target = "id", qualifiedByName = "uuidToIdentityId")
    @Mapping(source = "id", target = "internalId")
    @Mapping(source = "provider", target = "provider", qualifiedByName = "stringToProvider")
    @Mapping(source = "isDeleted", target = "deleted")
    Identity toDomain(IdentityJpaEntity entity);

    @Named("uuidToIdentityId")
    default IdentityId uuidToIdentityId(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return new IdentityId(uuid);
    }

    @Named("providerToString")
    default String providerToString(SocialProvider provider) {
        if (provider == null) {
            return null;
        }
        return provider.name();
    }

    @Named("stringToProvider")
    default SocialProvider stringToProvider(String provider) {
        if (provider == null) {
            return null;
        }
        return SocialProvider.valueOf(provider);
    }
}

