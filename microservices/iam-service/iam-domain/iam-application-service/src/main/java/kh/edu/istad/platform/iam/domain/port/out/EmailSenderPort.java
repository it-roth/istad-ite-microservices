package kh.edu.istad.platform.iam.domain.port.out;

public interface EmailSenderPort {

    /**
     * Sends an HTML email to the given recipient.
     *
     * @param toEmail   Recipient email address
     * @param subject   Email subject line
     * @param bodyHtml  HTML content of the email
     */
    void sendEmail(String toEmail, String subject, String bodyHtml);

    /**
     * Sends a one-time verification code (OTP) for security operations (2FA, Password Reset, etc.).
     *
     * @param toEmail Recipient email address
     * @param code    6-digit numeric OTP code
     * @param purpose Human-readable purpose (e.g. "Two-Factor Authentication", "Password Reset")
     */
    void sendVerificationOtp(String toEmail, String code, String purpose);
}
