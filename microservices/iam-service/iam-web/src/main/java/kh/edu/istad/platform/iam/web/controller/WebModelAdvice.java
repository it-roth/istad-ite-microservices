package kh.edu.istad.platform.iam.web.controller;

import kh.edu.istad.platform.iam.web.i18n.I18nMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

@ControllerAdvice(basePackages = "kh.edu.istad.platform.iam.web.controller")
@RequiredArgsConstructor
public class WebModelAdvice {

    private final MessageSource messageSource;

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

