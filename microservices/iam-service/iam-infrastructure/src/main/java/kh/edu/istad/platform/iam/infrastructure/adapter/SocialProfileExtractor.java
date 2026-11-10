package kh.edu.istad.platform.iam.infrastructure.adapter;

import kh.edu.istad.platform.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.platform.iam.domain.valueobject.SocialProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SocialProfileExtractor {

    public FederatedIdentityCommand extract(String registrationId, Map<String, Object> attributes) {
        String regId = registrationId.toLowerCase().trim();

        SocialProvider provider;
        String subject;
        String email = null;
        Boolean emailVerified = false;
        String displayName = null;
        String username = null;
        String pictureUrl = null;

        switch (regId) {
            case "google":
                provider = SocialProvider.GOOGLE;
                subject = String.valueOf(attributes.getOrDefault("sub", attributes.get("id")));
                email = (String) attributes.get("email");
                Object emailVer = attributes.get("email_verified");
                if (emailVer instanceof Boolean b) {
                    emailVerified = b;
                } else if (emailVer instanceof String s) {
                    emailVerified = Boolean.parseBoolean(s);
                }
                displayName = (String) attributes.get("name");
                pictureUrl = (String) attributes.get("picture");
                break;

            case "github":
                provider = SocialProvider.GITHUB;
                subject = String.valueOf(attributes.get("id"));
                email = (String) attributes.get("email");
                emailVerified = false;
                displayName = (String) attributes.get("name");
                username = (String) attributes.get("login");
                pictureUrl = (String) attributes.get("avatar_url");
                break;

            case "facebook":
                provider = SocialProvider.FACEBOOK;
                subject = String.valueOf(attributes.get("id"));
                email = (String) attributes.get("email");
                emailVerified = false;
                displayName = (String) attributes.get("name");
                pictureUrl = extractFacebookPicture(attributes);
                break;

            case "telegram":
                provider = SocialProvider.TELEGRAM;
                subject = String.valueOf(attributes.get("id"));
                email = (String) attributes.get("email");
                emailVerified = false;
                String firstName = (String) attributes.get("first_name");
                String lastName = (String) attributes.get("last_name");
                displayName = buildDisplayName(firstName, lastName);
                username = (String) attributes.get("username");
                pictureUrl = (String) attributes.get("photo_url");
                break;

            default:
                throw new IllegalArgumentException("Unsupported social login provider: " + registrationId);
        }

        return new FederatedIdentityCommand(
                provider,
                subject,
                email,
                emailVerified,
                displayName,
                username,
                pictureUrl,
                attributes
        );
    }

    private String buildDisplayName(String first, String last) {
        if (first == null && last == null) {
            return null;
        }
        if (first != null && last != null) {
            return first + " " + last;
        }
        return first != null ? first : last;
    }

    @SuppressWarnings("unchecked")
    private String extractFacebookPicture(Map<String, Object> attributes) {
        Object pictureObj = attributes.get("picture");
        if (pictureObj instanceof Map<?, ?> picMap) {
            Object dataObj = picMap.get("data");
            if (dataObj instanceof Map<?, ?> dataMap) {
                return (String) dataMap.get("url");
            }
        }
        return null;
    }
}
