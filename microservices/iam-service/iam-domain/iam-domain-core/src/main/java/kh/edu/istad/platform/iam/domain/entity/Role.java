package kh.edu.istad.platform.iam.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.platform.iam.domain.valueobject.RoleId;

import java.time.LocalDateTime;

public class Role extends AggregateRoot<RoleId> {
    private Long internalId;
    private String name;
    private String description;
    private Boolean isSystem;
    private Boolean isActive;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Long version;

    private Role(Builder builder) {
        super.setId(builder.id);
        this.internalId = builder.internalId;
        this.name = builder.name;
        this.description = builder.description;
        this.isSystem = builder.isSystem;
        this.isActive = builder.isActive;
        this.isDeleted = builder.isDeleted;
        this.deletedAt = builder.deletedAt;
        this.deletedBy = builder.deletedBy;
        this.createdAt = builder.createdAt;
        this.createdBy = builder.createdBy;
        this.updatedAt = builder.updatedAt;
        this.updatedBy = builder.updatedBy;
        this.version = builder.version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getInternalId() {
        return internalId;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Boolean getSystem() {
        return isSystem;
    }
    public Boolean getActive() {
        return isActive;
    }
    public Boolean getDeleted() {
        return isDeleted;
    }
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    public String getDeletedBy() {
        return deletedBy;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public Long getVersion() {
        return version;
    }

    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setSystem(Boolean system) {
        isSystem = system;
    }
    public void setActive(Boolean active) {
        isActive = active;
    }
    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    public void setVersion(Long version) {
        this.version = version;
    }

    public static final class Builder {
        private RoleId id;
        private Long internalId;
        private String name;
        private String description;
        private Boolean isSystem;
        private Boolean isActive;
        private Boolean isDeleted;
        private LocalDateTime deletedAt;
        private String deletedBy;
        private LocalDateTime createdAt;
        private String createdBy;
        private LocalDateTime updatedAt;
        private String updatedBy;
        private Long version;

        private Builder() {
        }

        public Builder id(RoleId val) {
        id = val;
        return this;
    }
        public Builder internalId(Long val) {
        internalId = val;
        return this;
    }
        public Builder name(String val) {
        name = val;
        return this;
    }
        public Builder description(String val) {
        description = val;
        return this;
    }
        public Builder isSystem(Boolean val) {
            isSystem = val;
            return this;
        }

        public Builder system(Boolean val) {
            isSystem = val;
            return this;
        }

        public Builder isActive(Boolean val) {
            isActive = val;
            return this;
        }

        public Builder active(Boolean val) {
            isActive = val;
            return this;
        }

        public Builder isDeleted(Boolean val) {
            isDeleted = val;
            return this;
        }

        public Builder deleted(Boolean val) {
            isDeleted = val;
            return this;
        }
        public Builder deletedAt(LocalDateTime val) {
        deletedAt = val;
        return this;
    }
        public Builder deletedBy(String val) {
        deletedBy = val;
        return this;
    }
        public Builder createdAt(LocalDateTime val) {
        createdAt = val;
        return this;
    }
        public Builder createdBy(String val) {
        createdBy = val;
        return this;
    }
        public Builder updatedAt(LocalDateTime val) {
        updatedAt = val;
        return this;
    }
        public Builder updatedBy(String val) {
        updatedBy = val;
        return this;
    }
        public Builder version(Long val) {
        version = val;
        return this;
    }

        public Role build() {
        return new Role(this);
    }
    }
}


