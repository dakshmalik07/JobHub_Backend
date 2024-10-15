package com.backend.jobHub.service;

import com.backend.jobHub.entity.EmailCampaign;
import com.backend.jobHub.repository.EmailCampaignRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailCampaignRepository emailCampaignRepository;

    private JavaMailSender createJavaMailSender(String email, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(email);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }

    public void sendVerificationEmail(String recipientEmail, String token) {
        String staticVerificationEmail = "jhub2254@gmail.com";
        String staticVerificationEmailPassword = "Vasu123@";
        JavaMailSender mailSender = createJavaMailSender(staticVerificationEmail, staticVerificationEmailPassword);
        String verificationLink = "http://localhost:3000/validate?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(staticVerificationEmail);
        message.setTo(recipientEmail);
        message.setSubject("Email Verification");
        message.setText("Please click the following link to verify your email: " + verificationLink);
        mailSender.send(message);
    }

    public void sendCampaignEmail(String senderEmail, String senderPassword, String recipient, String subject, String bodyContent) {
        JavaMailSender mailSender = createJavaMailSender(senderEmail, senderPassword);
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(senderEmail);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(bodyContent, true);
            mailSender.send(message);
            System.out.println("Email sent to " + recipient);

        } catch (MessagingException e) {
            System.err.println("Error while sending email: " + e.getMessage());
        }
    }

    public String createCampaign(String senderEmail, String senderPassword, List<String> recipients, String subject, String bodyContent) {
        EmailCampaign campaign = new EmailCampaign();
        campaign.setSenderEmail(senderEmail);
        campaign.setSubject(subject);
        campaign.setBodyContent(bodyContent);
        campaign.setCreatedAt(LocalDateTime.now().toString());
        emailCampaignRepository.save(campaign);

        for (String recipient : recipients) {
            sendCampaignEmail(senderEmail, senderPassword, recipient, subject, bodyContent);
        }
        return "Email campaign created successfully!";
    }
}
