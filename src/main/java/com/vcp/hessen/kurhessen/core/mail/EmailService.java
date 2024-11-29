package com.vcp.hessen.kurhessen.core.mail;

import jakarta.mail.MessagingException;
import org.springframework.mail.SimpleMailMessage;

import java.io.File;
import java.util.List;

public interface EmailService {
    void sendSimpleMessage(
            String to, String subject, String text, boolean html) throws MessagingException;

    void sendMessageWithAttachment(String to, String subject, String text, boolean html, File attachment) throws MessagingException;
    void sendMessageWithAttachments(String to, String subject, String text, boolean html, List<File> attachment) throws MessagingException;
}
