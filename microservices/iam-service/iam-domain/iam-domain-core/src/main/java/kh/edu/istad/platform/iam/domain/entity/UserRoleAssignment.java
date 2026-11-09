package kh.edu.istad.platform.iam.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;

import java.time.LocalDateTime;

public class UserRoleAssignment extends BaseEntity<Long> {
    private Long internalId;
    private Long userId;
    private Long roleId;
    private LocalDateTime assignedAt;
    private String assignedBy;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private String revokedBy;
    private String reason;
    private Long version;

    private UserRoleAssignment(Builder builder) {
        super.setId(builder.id);
        this.internalId = builder.internalId;
        this.userId = builder.userId;
        this.roleId = builder.roleId;
        this.assignedAt = builder.assignedAt;
        this.assignedBy = builder.assignedBy;
        this.expiresAt = builder.expiresAt;
        this.revokedAt = builder.revokedAt;
        this.revokedBy = builder.revokedBy;
        this.reason = builder.reason;
        this.version = builder.version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isActive() {
        return revokedAt == null && (expiresAt == null || expiresAt.isAfter(LocalDateTime.now()));
    }

    public void revoke(String actor, String reason) {
        this.revokedAt = LocalDateTime.now();
        this.revokedBy = actor;
        this.reason = reason;
    }

    public Long getInternalId() {
        return internalId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public String getRevokedBy() {
        return revokedBy;
    }

    public String getReason() {
        return reason;
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

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public void setRevokedBy(String revokedBy) {
        this.revokedBy = revokedBy;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public static final class Builder {
        private Long id;
        private Long internalId;
        private Long userId;
        private Long roleId;
        private LocalDateTime assignedAt;
        private String assignedBy;
        private LocalDateTime expiresAt;
        private LocalDateTime revokedAt;
        private String revokedBy;
        private String reason;
        private Long version;

        private Builder() {
        }

        public Builder id(Long val) {
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
        public Builder roleId(Long val) {
            roleId = val;
            return this;
        }
        public Builder assignedAt(LocalDateTime val) {
            assignedAt = val;
            return this;
        }
        public Builder assignedBy(String val) {
            assignedBy = val;
            return this;
        }
        public Builder expiresAt(LocalDateTime val) {
            expiresAt = val;
            return this;
        }
        public Builder revokedAt(LocalDateTime val) {
            revokedAt = val;
            return this;
        }
        public Builder revokedBy(String val) {
            revokedBy = val;
            return this;
        }
        public Builder reason(String val) {
            reason = val;
            return this;
        }
        public Builder version(Long val) {
            version = val;
            return this;
        }

        public UserRoleAssignment build() {
            return new UserRoleAssignment(this);
        }
    }
}
