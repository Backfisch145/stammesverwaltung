package com.vcp.hessen.kurhessen.core.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.sender}")
    private String sender;
    @Value("${spring.mail.enabled}")
    private boolean mailsEnabled;

    @Override
    public void sendSimpleMessage(
            String to, String subject, String content, boolean html) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();

        message.setSubject(subject);
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(message, true);
        helper.setFrom(sender);
        helper.setTo(to);
        helper.setText(content, html);

        if (mailsEnabled) {
            emailSender.send(message);
        } else {
            log.warn("sendSimpleMessage: Mails are not enabled!");
        }

        log.info("sendSimpleMessage: Mail to {} with subject {} was sent!", to, subject);
    }

    @Override
    public void sendMessageWithAttachment(
            String to, String subject, String content, boolean html, File attachment) throws MessagingException {

        sendMessageWithAttachments(to, subject, content, html, List.of(attachment));
    }
    @Override
    public void sendMessageWithAttachments(
            String to, String subject, String content, boolean html, List<File> attachments) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(sender);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, html);

        for (File attachment : attachments) {
            FileSystemResource file
                    = new FileSystemResource(attachment);
            helper.addAttachment(attachment.getName(), file);
        }

        if (mailsEnabled) {
            emailSender.send(message);
        } else {
            log.warn("sendMessageWithAttachments: Mails are not enabled!");
        }
    }
}
