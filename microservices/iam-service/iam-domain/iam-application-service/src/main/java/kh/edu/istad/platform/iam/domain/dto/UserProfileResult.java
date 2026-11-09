package kh.edu.istad.platform.iam.domain.dto;

import kh.edu.istad.platform.iam.domain.entity.User;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class UserProfileResult {
    private final UUID id;
    private final String username;
    private final String email;
    private final Boolean emailVerified;
    private final String givenName;
    private final String familyName;
    private final String middleName;
    private final String nickname;
    private final String gender;
    private final LocalDate birthdate;
    private final String profileUrl;
    private final String pictureUrl;
    private final String websiteUrl;
    private final String zoneinfo;
    private final String locale;
    private final String phoneNumber;
    private final String addressStreetAddress;
    private final String addressLocality;
    private final String addressRegion;
    private final String addressPostalCode;
    private final String addressCountry;

    public UserProfileResult(User user) {
        this.id = user.getId().value();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.emailVerified = user.getEmailVerified();
        this.givenName = user.getGivenName();
        this.familyName = user.getFamilyName();
        this.middleName = user.getMiddleName();
        this.nickname = user.getNickname();
        this.gender = user.getGender();
        this.birthdate = user.getBirthdate();
        this.profileUrl = user.getProfileUrl();
        this.pictureUrl = user.getPictureUrl();
        this.websiteUrl = user.getWebsiteUrl();
        this.zoneinfo = user.getZoneinfo();
        this.locale = user.getLocale();
        this.phoneNumber = user.getPhoneNumber();
        this.addressStreetAddress = user.getAddressStreetAddress();
        this.addressLocality = user.getAddressLocality();
        this.addressRegion = user.getAddressRegion();
        this.addressPostalCode = user.getAddressPostalCode();
        this.addressCountry = user.getAddressCountry();
    }
}
