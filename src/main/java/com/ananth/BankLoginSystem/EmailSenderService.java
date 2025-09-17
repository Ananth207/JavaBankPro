package com.ananth.BankLoginSystem;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Random;


@Service

public class EmailSenderService{
    @Autowired
    public JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body, int otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ananth.narayanan207@gmail.com");
        message.setTo(toEmail);
        message.setText(body+otp);
        message.setSubject(subject);

        mailSender.send(message);

        System.out.println("Mail Sent successfully");

    }

}
