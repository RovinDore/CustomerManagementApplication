package com.management.CustomerManagement.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    private final Logger logger;

    public EmailService() {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Async
    public void sendMail(String toEmail, String subject, String body) throws InterruptedException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("admin@customermanage.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
//        Thread.sleep(5000L);
        mailSender.send(message);
        logger.info("Email sent successfully to " + toEmail);
    }
}
