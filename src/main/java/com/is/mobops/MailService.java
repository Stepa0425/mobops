package com.is.mobops;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
@Service
public class MailService {
    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendPdfByEmail(String to, String subject, String text, byte[] pdfData, String pdfFileName) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // Прикрепление PDF-файла к сообщению
            ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfData, "application/pdf");
            helper.addAttachment(pdfFileName, dataSource);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Обработка ошибки отправки электронной почты
        }
    }
}