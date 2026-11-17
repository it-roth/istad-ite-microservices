package kh.edu.istad.platform.iam.domain.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class VerifyTwoFactorOtpUseCase {

    public boolean execute(String username, String email, String code) {
        return SendTwoFactorOtpUseCase.verifyCode(username, email, code);
    }
}
