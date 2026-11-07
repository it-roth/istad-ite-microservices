package kh.edu.istad.platform.iam.domain.entity;

import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.platform.iam.domain.valueobject.UserId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User extends AggregateRoot<UserId> {
    private Long internalId;
    private String username;
    private String email;
    private Boolean emailVerified;
    private String passwordHash;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickname;
    private String gender;
    private LocalDate birthdate;
    private String profileUrl;
    private String pictureUrl;
    private String websiteUrl;
    private String zoneinfo;
    private String locale;
    private String phoneNumber;
    private Boolean phoneNumberVerified;

    private String addressStreetAddress;
    private String addressLocality;
    private String addressRegion;
    private String addressPostalCode;
    private String addressCountry;

    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Integer failedLoginAttempts;
    private LocalDateTime lastLoginAt;
    private LocalDateTime passwordChangedAt;

    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Long version;

    private User(Builder builder) {
        super.setId(builder.id);
        this.internalId = builder.internalId;
        this.username = builder.username;
        this.email = builder.email;
        this.emailVerified = builder.emailVerified;
        this.passwordHash = builder.passwordHash;
        this.givenName = builder.givenName;
        this.familyName = builder.familyName;
        this.middleName = builder.middleName;
        this.nickname = builder.nickname;
        this.gender = builder.gender;
        this.birthdate = builder.birthdate;
        this.profileUrl = builder.profileUrl;
        this.pictureUrl = builder.pictureUrl;
        this.websiteUrl = builder.websiteUrl;
        this.zoneinfo = builder.zoneinfo;
        this.locale = builder.locale;
        this.phoneNumber = builder.phoneNumber;
        this.phoneNumberVerified = builder.phoneNumberVerified;
        this.addressStreetAddress = builder.addressStreetAddress;
        this.addressLocality = builder.addressLocality;
        this.addressRegion = builder.addressRegion;
        this.addressPostalCode = builder.addressPostalCode;
        this.addressCountry = builder.addressCountry;
        this.enabled = builder.enabled;
        this.accountNonExpired = builder.accountNonExpired;
        this.accountNonLocked = builder.accountNonLocked;
        this.credentialsNonExpired = builder.credentialsNonExpired;
        this.failedLoginAttempts = builder.failedLoginAttempts;
        this.lastLoginAt = builder.lastLoginAt;
        this.passwordChangedAt = builder.passwordChangedAt;
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

    public String getFullName() {
        return (givenName != null ? givenName : "") + " " + (familyName != null ? familyName : "");
    }

    // Getters
    public Long getInternalId() {
        return internalId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getZoneinfo() {
        return zoneinfo;
    }

    public String getLocale() {
        return locale;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Boolean getPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public String getAddressStreetAddress() {
        return addressStreetAddress;
    }

    public String getAddressLocality() {
        return addressLocality;
    }

    public String getAddressRegion() {
        return addressRegion;
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
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


    // Setters
    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhoneNumberVerified(Boolean phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public void setAddressStreetAddress(String addressStreetAddress) {
        this.addressStreetAddress = addressStreetAddress;
    }

    public void setAddressLocality(String addressLocality) {
        this.addressLocality = addressLocality;
    }

    public void setAddressRegion(String addressRegion) {
        this.addressRegion = addressRegion;
    }

    public void setAddressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
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
        private UserId id;
        private Long internalId;
        private String username;
        private String email;
        private Boolean emailVerified;
        private String passwordHash;
        private String givenName;
        private String familyName;
        private String middleName;
        private String nickname;
        private String gender;
        private LocalDate birthdate;
        private String profileUrl;
        private String pictureUrl;
        private String websiteUrl;
        private String zoneinfo;
        private String locale;
        private String phoneNumber;
        private Boolean phoneNumberVerified;
        private String addressStreetAddress;
        private String addressLocality;
        private String addressRegion;
        private String addressPostalCode;
        private String addressCountry;
        private Boolean enabled;
        private Boolean accountNonExpired;
        private Boolean accountNonLocked;
        private Boolean credentialsNonExpired;
        private Integer failedLoginAttempts;
        private LocalDateTime lastLoginAt;
        private LocalDateTime passwordChangedAt;
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

        public Builder id(UserId val) {
            id = val;
            return this;
        }
        public Builder internalId(Long val) {
            internalId = val;
            return this;
        }
        public Builder username(String val) {
            username = val;
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
        public Builder passwordHash(String val) {
            passwordHash = val;
            return this;
        }
        public Builder givenName(String val) {
            givenName = val;
            return this;
        }
        public Builder familyName(String val) {
            familyName = val;
            return this;
        }
        public Builder middleName(String val) {
            middleName = val;
            return this;
        }
        public Builder nickname(String val) {
            nickname = val;
            return this;
        }
        public Builder gender(String val) {
            gender = val;
            return this;
        }
        public Builder birthdate(LocalDate val) {
            birthdate = val;
            return this;
        }
        public Builder profileUrl(String val) {
            profileUrl = val;
            return this;
        }
        public Builder pictureUrl(String val) {
            pictureUrl = val;
            return this;
        }
        public Builder websiteUrl(String val) {
            websiteUrl = val;
            return this;
        }
        public Builder zoneinfo(String val) {
            zoneinfo = val;
            return this;
        }
        public Builder locale(String val) {
            locale = val;
            return this;
        }
        public Builder phoneNumber(String val) {
            phoneNumber = val;
            return this;
        }
        public Builder phoneNumberVerified(Boolean val) {
            phoneNumberVerified = val;
            return this;
        }
        public Builder addressStreetAddress(String val) {
            addressStreetAddress = val;
            return this;
        }
        public Builder addressLocality(String val) {
            addressLocality = val;
            return this;
        }
        public Builder addressRegion(String val) {
            addressRegion = val;
            return this;
        }
        public Builder addressPostalCode(String val) {
            addressPostalCode = val;
            return this;
        }
        public Builder addressCountry(String val) {
            addressCountry = val;
            return this;
        }
        public Builder enabled(Boolean val) {
            enabled = val;
            return this;
        }
        public Builder accountNonExpired(Boolean val) {
            accountNonExpired = val;
            return this;
        }
        public Builder accountNonLocked(Boolean val) {
            accountNonLocked = val;
            return this;
        }
        public Builder credentialsNonExpired(Boolean val) {
            credentialsNonExpired = val;
            return this;
        }
        public Builder failedLoginAttempts(Integer val) {
            failedLoginAttempts = val;
            return this;
        }
        public Builder lastLoginAt(LocalDateTime val) {
            lastLoginAt = val;
            return this;
        }
        public Builder passwordChangedAt(LocalDateTime val) {
            passwordChangedAt = val;
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

        public User build() {
            return new User(this);
        }
    }
}

