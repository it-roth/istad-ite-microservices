package kh.edu.istad.platform.iam.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;

import java.time.LocalDateTime;

public class RolePermissionAssignment extends BaseEntity<Long> {
    private Long internalId;
    private Long roleId;
    private Long permissionId;
    private LocalDateTime assignedAt;
    private String assignedBy;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private String revokedBy;
    private String reason;

    private RolePermissionAssignment(Builder builder) {
        super.setId(builder.id);
        this.internalId = builder.internalId;
        this.roleId = builder.roleId;
        this.permissionId = builder.permissionId;
        this.assignedAt = builder.assignedAt;
        this.assignedBy = builder.assignedBy;
        this.expiresAt = builder.expiresAt;
        this.revokedAt = builder.revokedAt;
        this.revokedBy = builder.revokedBy;
        this.reason = builder.reason;
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

    public Long getRoleId() {
        return roleId;
    }

    public Long getPermissionId() {
        return permissionId;
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


    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
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


    public static final class Builder {
        private Long id;
        private Long internalId;
        private Long roleId;
        private Long permissionId;
        private LocalDateTime assignedAt;
        private String assignedBy;
        private LocalDateTime expiresAt;
        private LocalDateTime revokedAt;
        private String revokedBy;
        private String reason;

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
        public Builder roleId(Long val) {
            roleId = val;
            return this;
        }
        public Builder permissionId(Long val) {
            permissionId = val;
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

        public RolePermissionAssignment build() {
            return new RolePermissionAssignment(this);
        }
    }
}

