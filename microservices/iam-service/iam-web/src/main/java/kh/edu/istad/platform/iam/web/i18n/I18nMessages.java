package kh.edu.istad.platform.iam.web.i18n;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class I18nMessages {

    private final MessageSource messageSource;
    private final Locale locale;

    public I18nMessages(MessageSource messageSource, Locale locale) {
        this.messageSource = messageSource;
        this.locale = locale != null ? locale : Locale.ENGLISH;
    }

    public String get(String code) {
        if (code == null) {
            return "";
        }
        return messageSource.getMessage(code, null, code, locale);
    }

    public String get(String code, Object... args) {
        if (code == null) {
            return "";
        }
        return messageSource.getMessage(code, args, code, locale);
    }
}

