package kh.edu.istad.platform.iam.domain.service;

import kh.edu.istad.platform.iam.domain.dto.SecurityConfigurationResult;
import kh.edu.istad.platform.iam.domain.exception.IamDomainException;
import kh.edu.istad.platform.iam.domain.port.out.AccountSecurityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PasswordPolicyValidator {

    private final AccountSecurityRepository accountSecurityRepository;

    public void validate(String password) {
        if (password == null || password.isBlank()) {
            throw new IamDomainException("Password is required");
        }

        SecurityConfigurationResult config = null;
        try {
            config = accountSecurityRepository.getSecurityConfiguration();
        } catch (Exception ex) {
            log.warn("Could not load security configuration, using defaults: {}", ex.getMessage());
        }

        int minLength = 8;
        boolean requireUppercase = false;
        boolean requireNumber = false;
        boolean requireSymbol = false;

        if (config != null) {
            if (config.passwordMinimumLength() > 0) {
                minLength = config.passwordMinimumLength();
            }
            requireUppercase = config.passwordRequireUppercase();
            requireNumber = config.passwordRequireNumber();
            requireSymbol = config.passwordRequireSymbol();
        }

        if (password.length() < minLength) {
            throw new IamDomainException("Password must be at least " + minLength + " characters");
        }

        if (requireUppercase && !password.chars().anyMatch(Character::isUpperCase)) {
            throw new IamDomainException("Password must contain at least one uppercase letter (A-Z)");
        }

        if (requireNumber && !password.chars().anyMatch(Character::isDigit)) {
            throw new IamDomainException("Password must contain at least one numeric digit (0-9)");
        }

        if (requireSymbol) {
            boolean hasSymbol = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;':\",./<>?~`".indexOf(ch) >= 0);
            if (!hasSymbol) {
                throw new IamDomainException("Password must contain at least one special character symbol (!@#$%^&*)");
            }
        }
    }
}
