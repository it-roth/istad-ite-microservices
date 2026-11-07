package kh.edu.istad.platform.iam.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.platform.iam.domain.valueobject.IdentityId;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;

import java.time.LocalDateTime;
import java.util.Map;

public class Identity extends BaseEntity<IdentityId> {
    private Long internalId;
    private Long userId;
    private SocialProvider provider;
    private String providerSubject;
    private String email;
    private Boolean emailVerified;
    private String displayName;
    private String username;
    private String pictureUrl;
    private Map<String, Object> attributes;
    private LocalDateTime linkedAt;
    private LocalDateTime lastLoginAt;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Long version;

    private Identity(Builder builder) {
        super.setId(builder.id);
        this.internalId = builder.internalId;
        this.userId = builder.userId;
        this.provider = builder.provider;
        this.providerSubject = builder.providerSubject;
        this.email = builder.email;
        this.emailVerified = builder.emailVerified;
        this.displayName = builder.displayName;
        this.username = builder.username;
        this.pictureUrl = builder.pictureUrl;
        this.attributes = builder.attributes;
        this.linkedAt = builder.linkedAt;
        this.lastLoginAt = builder.lastLoginAt;
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

    public Long getUserId() {
        return userId;
    }

    public SocialProvider getProvider() {
        return provider;
    }

    public String getProviderSubject() {
        return providerSubject;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public LocalDateTime getLinkedAt() {
        return linkedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProvider(SocialProvider provider) {
        this.provider = provider;
    }

    public void setProviderSubject(String providerSubject) {
        this.providerSubject = providerSubject;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setLinkedAt(LocalDateTime linkedAt) {
        this.linkedAt = linkedAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
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
        private IdentityId id;
        private Long internalId;
        private Long userId;
        private SocialProvider provider;
        private String providerSubject;
        private String email;
        private Boolean emailVerified;
        private String displayName;
        private String username;
        private String pictureUrl;
        private Map<String, Object> attributes;
        private LocalDateTime linkedAt;
        private LocalDateTime lastLoginAt;
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

        public Builder id(IdentityId val) {
            id = val;
            return this;
        }
        public Builder internalId(Long val) {
            internalId = val;
            return this;
        }
        public Builder userId(Long val) {
            userId = val;
            return this;
        }
        public Builder provider(SocialProvider val) {
            provider = val;
            return this;
        }
        public Builder providerSubject(String val) {
            providerSubject = val;
            return this;
        }
        public Builder email(String val) {
            email = val;
            return this;
        }
        public Builder emailVerified(Boolean val) {
            emailVerified = val;
            return this;
        }
        public Builder displayName(String val) {
            displayName = val;
            return this;
        }
        public Builder username(String val) {
            username = val;
            return this;
        }
        public Builder pictureUrl(String val) {
            pictureUrl = val;
            return this;
        }
        public Builder attributes(Map<String, Object> val) {
            attributes = val;
            return this;
        }
        public Builder linkedAt(LocalDateTime val) {
            linkedAt = val;
            return this;
        }
        public Builder lastLoginAt(LocalDateTime val) {
            lastLoginAt = val;
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

        public Identity build() {
            return new Identity(this);
        }
    }
}

