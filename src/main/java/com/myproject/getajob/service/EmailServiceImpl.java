package com.myproject.getajob.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        String verificationUrl = "http://localhost:3000/verify-email?token=" + token;

        // HTML Template
        String htmlContent = "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;\">"
                +
                "<div style=\"text-align: center; padding-bottom: 20px; border-bottom: 2px solid #0d6efd;\">" +
                "<h1 style=\"color: #0d6efd; margin: 0; font-size: 28px;\">GetAJob</h1>" +
                "<p style=\"color: #6c757d; margin: 5px 0 0; font-size: 14px;\">Kariyer Yolculuğun Burada Başlar</p>" +
                "</div>" +
                "<div style=\"padding: 40px 20px; text-align: center;\">" +
                "<h2 style=\"color: #333; margin-top: 0;\">Aramıza Hoş Geldin!</h2>" +
                "<p style=\"color: #555; font-size: 16px; line-height: 1.6; margin-bottom: 30px;\">" +
                "GetAJob hesabını oluşturduğun için teşekkürler. Hesabının güvenliği ve aktif kullanımı için lütfen aşağıdaki butona tıklayarak e-posta adresini doğrula."
                +
                "</p>" +
                "<a href=\"" + verificationUrl
                + "\" style=\"display: inline-block; padding: 14px 28px; font-size: 16px; font-weight: bold; color: #ffffff; background-color: #0d6efd; text-decoration: none; border-radius: 50px; box-shadow: 0 4px 6px rgba(13, 110, 253, 0.2); transition: background 0.3s;\">"
                +
                "Hesabımı Doğrula" +
                "</a>" +
                "<p style=\"margin-top: 30px; font-size: 14px; color: #888;\">" +
                "Eğer butona tıklayamıyorsan, aşağıdaki bağlantıyı tarayıcına kopyala:<br>" +
                "<a href=\"" + verificationUrl + "\" style=\"color: #0d6efd; word-break: break-all;\">"
                + verificationUrl + "</a>" +
                "</p>" +
                "</div>" +
                "<div style=\"text-align: center; padding-top: 20px; border-top: 1px solid #e0e0e0; font-size: 12px; color: #999;\">"
                +
                "<p>&copy; 2025 GetAJob. Tüm Hakları Saklıdır.</p>" +
                "<p>Bu e-postayı siz talep etmediyseniz, lütfen dikkate almayınız.</p>" +
                "</div>" +
                "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("verify@getajob.com", "GetAJob Hesap Doğrulama");
            helper.setTo(java.util.Objects.requireNonNull(to));
            helper.setSubject("GetAJob - E-Posta Doğrulama");
            helper.setText(htmlContent, true); // true indicates HTML content

            mailSender.send(message);
            System.out.println("Doğrulama e-postası gönderildi: " + to);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println(
                    "E-posta gönderimi başarısız: " + (e.getMessage() != null ? e.getMessage() : "Bilinmiyor"));
            // Loglamak için veya kullanıcıya bildirmek için exception fırlatılabilir
            // throw new RuntimeException("Mail gönderimi başarısız", e);
            // Şimdilik sadece logluyoruz ki akış kesilmesin, ama üretimde kuyruğa atmak
            // mantıklı olur.
            e.printStackTrace();
        }
    }
}
