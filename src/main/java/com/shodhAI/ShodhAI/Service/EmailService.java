package com.shodhAI.ShodhAI.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Email Verification OTP");
            helper.setText(
                    "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;'>"
                            + "<div style='max-width: 500px; background: #ffffff; padding: 20px; border-radius: 8px; "
                            + "box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1); text-align: center;'>"
                            + "<h2 style='color: #333;'>ShodhAI OTP Verification</h2>"
                            + "<p style='font-size: 16px; color: #555;'>Your OTP for email verification is:</p>"
                            + "<h2 style='color: #d32f2f; font-size: 24px;'>" + otp + "</h2>"
                            + "<p style='font-size: 14px; color: #777;'>This OTP is valid for <strong>5 minutes</strong>. "
                            + "Do not share it with anyone.</p>"
                            + "<p style='font-size: 14px; color: #777;'>If you did not request this OTP, please ignore this email.</p>"
                            + "<hr style='margin: 20px 0; border: none; border-top: 1px solid #ddd;'>"
                            + "<p style='font-size: 12px; color: #888;'>Best regards,</p>"
                            + "<p style='font-size: 14px;'><strong>ShodhAI Team</strong></p>"
                            + "<p style='font-size: 14px;'><a href='https://shodhai.com' style='color: #007bff;'>www.shodhAI.com</a></p>"
                            + "</div></div>",
                    true
            );

            mailSender.send(message);
            System.out.println("OTP email sent to: " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
