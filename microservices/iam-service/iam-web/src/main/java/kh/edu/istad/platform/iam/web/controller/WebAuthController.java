package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.domain.dto.RegisterUserCommand;
import kh.edu.istad.platform.iam.domain.usecase.RegisterUserUseCase;
import kh.edu.istad.platform.iam.domain.usecase.SendTwoFactorOtpUseCase;
import kh.edu.istad.platform.iam.domain.usecase.VerifyTwoFactorOtpUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebAuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final SendTwoFactorOtpUseCase sendTwoFactorOtpUseCase;
    private final VerifyTwoFactorOtpUseCase verifyTwoFactorOtpUseCase;

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
            @RequestParam(name = "registered", required = false) String registered,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.core.Authentication authentication,
            java.util.Locale locale,
            Model model
    ) {
        model.addAttribute("currentLang", locale != null ? locale.getLanguage() : "en");

        if (logout != null) {
            model.addAttribute("logout", true);
        } else if (error != null) {
            model.addAttribute("error", true);
        } else if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            return "redirect:/account";
        }

        if (registered != null) {
            model.addAttribute("registered", true);
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.core.Authentication authentication
    ) {
        new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler()
                .logout(request, response, authentication);
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                session.invalidate();
            } catch (Exception ignored) {}
        }
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        jakarta.servlet.http.Cookie jsessionCookie = new jakarta.servlet.http.Cookie("JSESSIONID", "");
        jsessionCookie.setPath("/");
        jsessionCookie.setMaxAge(0);
        response.addCookie(jsessionCookie);
        jakarta.servlet.http.Cookie rmCookie = new jakarta.servlet.http.Cookie("remember-me", "");
        rmCookie.setPath("/");
        rmCookie.setMaxAge(0);
        response.addCookie(rmCookie);
        return "redirect:/login?logout";
    }

    @GetMapping("/login/2fa")
    public String twoFactorPage(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "target", required = false) String target,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "resent", required = false) String resent,
            java.util.Locale locale,
            Model model
    ) {
        model.addAttribute("currentLang", locale != null ? locale.getLanguage() : "en");
        String effectiveUsername = (username != null && !username.isBlank()) ? username : "admin";
        model.addAttribute("username", effectiveUsername);

        String effectiveEmail = (email != null && !email.isBlank()) ? email : (effectiveUsername + "@istad.edu.kh");
        model.addAttribute("email", effectiveEmail);

        if (target != null && !target.isBlank() && !target.contains("/login")) {
            model.addAttribute("target", target);
        } else {
            model.addAttribute("target", "/account");
        }

        if (error != null) {
            model.addAttribute("error", true);
        }
        if (resent != null) {
            model.addAttribute("resent", true);
        }

        // Trigger dispatching 2FA OTP code to the user's email address via SMTP
        try {
            sendTwoFactorOtpUseCase.execute(effectiveUsername, effectiveEmail, false);
        } catch (Exception ex) {
            log.error("Failed to dispatch 2FA OTP for {}: {}", effectiveUsername, ex.getMessage());
        }

        return "login-2fa";
    }

    @PostMapping("/login/2fa/resend")
    public String resendTwoFactor(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "target", required = false) String target
    ) {
        String effectiveUsername = (username != null && !username.isBlank()) ? username : "admin";
        String effectiveEmail = (email != null && !email.isBlank()) ? email : (effectiveUsername + "@istad.edu.kh");
        try {
            sendTwoFactorOtpUseCase.execute(effectiveUsername, effectiveEmail, true);
        } catch (Exception ex) {
            log.error("Failed to resend 2FA OTP for {}: {}", effectiveUsername, ex.getMessage());
        }
        String redirect = "redirect:/login/2fa?resent=true";
        if (username != null) redirect += "&username=" + java.net.URLEncoder.encode(username, java.nio.charset.StandardCharsets.UTF_8);
        if (email != null) redirect += "&email=" + java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8);
        if (target != null && !target.isBlank()) redirect += "&target=" + java.net.URLEncoder.encode(target, java.nio.charset.StandardCharsets.UTF_8);
        return redirect;
    }

    @PostMapping("/login/2fa")
    public String processTwoFactor(
            @RequestParam(name = "code") String code,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "target", required = false) String target,
            Model model
    ) {
        boolean valid = verifyTwoFactorOtpUseCase.execute(username, email, code);
        if (valid) {
            if (target != null && !target.isBlank() && !target.contains("/login")) {
                return "redirect:" + target;
            }
            return "redirect:/account";
        }
        String redirect = "redirect:/login/2fa?error=true";
        if (username != null) redirect += "&username=" + java.net.URLEncoder.encode(username, java.nio.charset.StandardCharsets.UTF_8);
        if (email != null) redirect += "&email=" + java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8);
        if (target != null && !target.isBlank()) redirect += "&target=" + java.net.URLEncoder.encode(target, java.nio.charset.StandardCharsets.UTF_8);
        return redirect;
    }

    @GetMapping("/register")
    public String registerPage(java.util.Locale locale, Model model) {
        model.addAttribute("currentLang", locale != null ? locale.getLanguage() : "en");
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("givenName") String givenName,
            @RequestParam("familyName") String familyName,
            @RequestParam(name = "locale", defaultValue = "en") String locale,
            Model model
    ) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("givenName", givenName);
            model.addAttribute("familyName", familyName);
            model.addAttribute("locale", locale);
            return "register";
        }

        try {
            RegisterUserCommand command = new RegisterUserCommand(
                    username,
                    email,
                    password,
                    givenName,
                    familyName,
                    locale
            );
            registerUserUseCase.execute(command);
            return "redirect:/login?registered=true";
        } catch (Exception ex) {
            log.error("Failed to register user: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("givenName", givenName);
            model.addAttribute("familyName", familyName);
            model.addAttribute("locale", locale);
            return "register";
        }
    }
}

