package kh.edu.istad.platform.iam.infrastructure.adapter;

import jakarta.mail.internet.MimeMessage;
import kh.edu.istad.platform.iam.domain.port.out.EmailSenderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class SmtpEmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.from:}")
    private String mailFrom;

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    public SmtpEmailSenderAdapter(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Override
    public void sendEmail(String toEmail, String subject, String bodyHtml) {
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Cannot send email: recipient address is empty");
            return;
        }

        if (mailSender == null) {
            log.warn("JavaMailSender bean is not available. Please verify spring-boot-starter-mail is present.");
            return;
        }

        if (mailUsername == null || mailUsername.isBlank()) {
            log.warn("SMTP credentials not provided in .env (MAIL_USERNAME/MAIL_PASSWORD is blank). "
                    + "Simulating email send to [{}] with subject [{}]", toEmail, subject);
            return;
        }

        try {
            log.info("Sending email via SMTP host {}:{} to [{}] with subject [{}]", mailHost, mailPort, toEmail, subject);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            String effectiveFrom = (mailFrom != null && !mailFrom.isBlank()) ? mailFrom : mailUsername;
            helper.setFrom(effectiveFrom, "ISTAD Identity & Access");
            helper.setTo(toEmail.trim());
            helper.setSubject(subject);
            helper.setText(bodyHtml, true);

            mailSender.send(mimeMessage);
            log.info("Successfully dispatched email via SMTP to [{}]", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send email via SMTP to [{}]: {}. Please check your MAIL_USERNAME and MAIL_PASSWORD configuration in .env.",
                    toEmail, ex.getMessage(), ex);
        }
    }

    @Override
    public void sendVerificationOtp(String toEmail, String code, String purpose) {
        String subject = "Your ISTAD Security Code: " + code;
        String formattedCode = (code != null && code.length() == 6)
                ? code.substring(0, 3) + " " + code.substring(3)
                : code;

        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>ISTAD Security Verification</title>
                  <style>
                    body { margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #0b0f19; color: #f1f5f9; }
                    .container { max-width: 560px; margin: 40px auto; background-color: #111827; border-radius: 16px; border: 1px solid #1f2937; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
                    .header { background: linear-gradient(135deg, #1e3a8a 0%%, #0f172a 100%%); padding: 32px 36px; text-align: center; border-bottom: 1px solid #1e293b; }
                    .header h1 { margin: 0; font-size: 20px; font-weight: 700; color: #ffffff; letter-spacing: -0.02em; }
                    .header p { margin: 6px 0 0; font-size: 13px; color: #94a3b8; }
                    .content { padding: 36px 36px 28px; }
                    .title { font-size: 18px; font-weight: 600; color: #f8fafc; margin-top: 0; margin-bottom: 12px; }
                    .text { font-size: 14px; line-height: 1.6; color: #cbd5e1; margin-bottom: 24px; }
                    .code-box { background-color: #0a0e17; border: 1px solid #2563eb; border-radius: 12px; padding: 20px; text-align: center; margin: 28px 0; }
                    .code { font-family: 'SF Mono', Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace; font-size: 32px; font-weight: 700; letter-spacing: 0.35em; color: #60a5fa; margin: 0; }
                    .code-hint { font-size: 12px; color: #64748b; margin-top: 8px; }
                    .warning { background-color: #18181b; border-left: 3px solid #f59e0b; padding: 12px 16px; border-radius: 0 8px 8px 0; margin-top: 24px; }
                    .warning p { margin: 0; font-size: 12px; color: #d4d4d8; line-height: 1.5; }
                    .footer { padding: 24px 36px; text-align: center; border-top: 1px solid #1f2937; background-color: #0d131f; }
                    .footer p { margin: 4px 0; font-size: 11px; color: #64748b; }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header">
                      <h1>ISTAD Identity & Access</h1>
                      <p>Security & Two-Factor Authentication</p>
                    </div>
                    <div class="content">
                      <h2 class="title">%s</h2>
                      <p class="text">
                        A verification code was requested for your ISTAD account. Use the code below to complete your authentication.
                      </p>
                      <div class="code-box">
                        <div class="code">%s</div>
                        <div class="code-hint">This code expires in 10 minutes</div>
                      </div>
                      <div class="warning">
                        <p><strong>Security Notice:</strong> Never share this verification code with anyone. ISTAD staff will never ask for your authentication codes.</p>
                      </div>
                    </div>
                    <div class="footer">
                      <p>&copy; 2026 ISTAD ITE Platform. All rights reserved.</p>
                      <p>Phnom Penh, Cambodia</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(purpose != null ? purpose : "Security Verification", formattedCode);

        sendEmail(toEmail, subject, html);
    }
}
