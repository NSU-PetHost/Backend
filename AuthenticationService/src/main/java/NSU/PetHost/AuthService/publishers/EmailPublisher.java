package NSU.PetHost.AuthService.publishers;

public interface EmailPublisher {

    void sendEmail(String toEmail, int verifyCode);

    void sendEmail(String toEmail, String subject, String body);

    void sendNotifyEmailResetPassword(String toEmail, int verifyCode);
}
