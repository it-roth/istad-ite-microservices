package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.web.i18n.I18nMessages;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

@ControllerAdvice(basePackages = "kh.edu.istad.platform.iam.web.controller")
@RequiredArgsConstructor
public class WebModelAdvice {

    private final MessageSource messageSource;

    @ModelAttribute("csrfToken")
    public CsrfToken csrfToken(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        try {
            // Eagerly ensure session exists BEFORE any template output commits the response
            request.getSession(true);
        } catch (Exception ignored) {
        }

        Object token = request.getAttribute(CsrfToken.class.getName());
        if (token instanceof CsrfToken ct) {
            // Force resolution of deferred token while response is not yet committed
            ct.getToken();
            return ct;
        }

        Object csrf = request.getAttribute("_csrf");
        if (csrf instanceof CsrfToken ct) {
            ct.getToken();
            return ct;
        }
        return null;
    }

    @ModelAttribute("msg")
    public I18nMessages addMessages(Locale locale) {
        return new I18nMessages(messageSource, locale);
    }

    @ModelAttribute("currentLang")
    public String currentLang(Locale locale) {
        if (locale == null || locale.getLanguage() == null) {
            return "en";
        }
        return locale.getLanguage();
    }

    @ModelAttribute("currentUser")
    public String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            return null;
        }
        return auth.getName();
    }
}

