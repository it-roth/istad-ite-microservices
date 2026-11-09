package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.domain.dto.ChangePasswordCommand;
import kh.edu.istad.platform.iam.domain.dto.IdentityResult;
import kh.edu.istad.platform.iam.domain.dto.OAuthClientResult;
import kh.edu.istad.platform.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.platform.iam.domain.dto.UserProfileResult;
import kh.edu.istad.platform.iam.domain.usecase.ChangePasswordUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetUserAuthoritiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.GetUserProfileUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListConnectedOAuthClientsUseCase;
import kh.edu.istad.platform.iam.domain.usecase.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iam.domain.usecase.RevokeOAuthClientConsentUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iam.domain.usecase.UpdateUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebProfileController {

    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;
    private final ListConnectedOAuthClientsUseCase listConnectedOAuthClientsUseCase;
    private final RevokeOAuthClientConsentUseCase revokeOAuthClientConsentUseCase;

    @GetMapping("/")
    public String index(
            Principal principal,
            @RequestParam(name = "passwordChanged", required = false) String passwordChanged,
            Model model
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        UserProfileResult user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        UUID userUuid = user.getId();
        UserAuthoritiesResult authorities = getUserAuthoritiesUseCase.execute(userUuid);

        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        for (String auth : authorities.effectiveAuthorities()) {
            if (auth.startsWith("ROLE_")) {
                roles.add(auth.substring(5));
            } else {
                permissions.add(auth);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("permissions", permissions);
        if (passwordChanged != null) {
            model.addAttribute("passwordChanged", true);
        }
        return "index";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(
            Principal principal,
            @RequestParam(name = "success", required = false) String success,
            Model model
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        UserProfileResult user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        if (success != null) {
            model.addAttribute("successMessage", "Profile updated successfully");
        }
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            Principal principal,
            @RequestParam("email") String email,
            @RequestParam("givenName") String givenName,
            @RequestParam("familyName") String familyName,
            @RequestParam(name = "middleName", required = false) String middleName,
            @RequestParam(name = "nickname", required = false) String nickname,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "birthdate", required = false) String birthdateStr,
            @RequestParam(name = "profileUrl", required = false) String profileUrl,
            @RequestParam(name = "pictureUrl", required = false) String pictureUrl,
            @RequestParam(name = "websiteUrl", required = false) String websiteUrl,
            @RequestParam(name = "zoneinfo", required = false) String zoneinfo,
            @RequestParam(name = "locale", required = false) String locale,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "addressStreetAddress", required = false) String addressStreetAddress,
            @RequestParam(name = "addressLocality", required = false) String addressLocality,
            @RequestParam(name = "addressRegion", required = false) String addressRegion,
            @RequestParam(name = "addressPostalCode", required = false) String addressPostalCode,
            @RequestParam(name = "addressCountry", required = false) String addressCountry,
            Model model
    ) {
        UserProfileResult user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        LocalDate birthdate = null;
        if (StringUtils.hasText(birthdateStr)) {
            try {
                birthdate = LocalDate.parse(birthdateStr.trim());
            } catch (Exception ignored) {
            }
        }

        try {
            UpdateUserProfileCommand command = new UpdateUserProfileCommand(
                    user.getId(),
                    email,
                    givenName,
                    familyName,
                    middleName,
                    nickname,
                    gender,
                    birthdate,
                    profileUrl,
                    pictureUrl,
                    websiteUrl,
                    zoneinfo,
                    locale,
                    phoneNumber,
                    addressStreetAddress,
                    addressLocality,
                    addressRegion,
                    addressPostalCode,
                    addressCountry
            );
            updateUserProfileUseCase.execute(command);
            return "redirect:/profile/edit?success=true";
        } catch (Exception ex) {
            log.error("Failed to update profile: {}", ex.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("submittedEmail", email);
            model.addAttribute("errorMessage", ex.getMessage());
            return "profile-edit";
        }
    }

    @GetMapping("/profile/password")
    public String changePasswordPage(Model model) {
        return "change-password";
    }

    @PostMapping("/profile/password")
    public String updatePassword(
            Principal principal,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model
    ) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match");
            return "change-password";
        }

        UserProfileResult user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            ChangePasswordCommand command = new ChangePasswordCommand(
                    user.getId(),
                    currentPassword,
                    newPassword
            );
            changePasswordUseCase.execute(command);
            return "redirect:/?passwordChanged=true";
        } catch (Exception ex) {
            log.error("Failed to change password: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            return "change-password";
        }
    }

    @GetMapping("/profile/accounts")
    public String linkedAccountsPage(
            Principal principal,
            @RequestParam(name = "unlinked", required = false) String unlinked,
            @RequestParam(name = "error", required = false) String error,
            Model model
    ) {
        UserProfileResult user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        List<IdentityResult> identities = listUserIdentitiesUseCase.execute(user.getId());
        model.addAttribute("identities", identities);
        if (unlinked != null) {
            model.addAttribute("successMessage", "Social account unlinked successfully");
        }
        if (error != null) {
            model.addAttribute("errorMessage", error);
        }
        return "linked-accounts";
    }

    @PostMapping("/profile/accounts/{identityUuid}/unlink")
    public String unlinkAccount(
            Principal principal,
            @PathVariable("identityUuid") UUID identityUuid
    ) {
        UserProfileResult user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            unlinkIdentityUseCase.execute(user.getId(), identityUuid);
            return "redirect:/profile/accounts?unlinked=true";
        } catch (Exception ex) {
            log.warn("Failed to unlink identity: {}", ex.getMessage());
            return "redirect:/profile/accounts?error=" + ex.getMessage();
        }
    }

    @GetMapping("/profile/apps")
    public String connectedAppsPage(
            Principal principal,
            @RequestParam(name = "revoked", required = false) String revoked,
            Model model
    ) {
            String principalName = principal.getName();
            List<OAuthClientResult> apps = listConnectedOAuthClientsUseCase.execute(principalName);
            model.addAttribute("apps", apps);
        if (revoked != null) {
            model.addAttribute("successMessage", "Application consent revoked successfully");
        }
        return "connected-apps";
    }

    @PostMapping("/profile/apps/{regClientId}/revoke")
    public String revokeAppConsent(
            Principal principal,
            @PathVariable("regClientId") String regClientId
    ) {
        revokeOAuthClientConsentUseCase.execute(regClientId, principal.getName());
        return "redirect:/profile/apps?revoked=true";
    }

    private UserProfileResult resolveUser(Principal principal) {
        return principal == null
                ? null
                : getUserProfileUseCase.execute(principal.getName()).orElse(null);
    }
}
