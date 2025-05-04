package NSU.PetHost.AuthService.services;

import NSU.PetHost.AuthService.publishers.EmailPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderService implements EmailPublisher {

    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender mailSender;
    private final SimpleMailMessage mailMessage;

    @Override
    public void sendEmail(String toEmail, int verifyCode) {

        mailMessage.setTo(toEmail);
        mailMessage.setSubject("PetHost confirm registration");
        mailMessage.setText("Registration confirmation code: " + verifyCode);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {

    }

    @Override
    public void sendNotifyEmailResetPassword(String toEmail, int verifyCode) {

        mailMessage.setTo(toEmail);
        mailMessage.setSubject("PetHost reset password");
        mailMessage.setText("Reset password confirmation code: " + verifyCode);
        mailMessage.setFrom(from);

        mailSender.send(mailMessage);

    }

}
