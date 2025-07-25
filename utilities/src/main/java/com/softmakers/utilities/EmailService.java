package com.softmakers.utilities;

import com.softmakers.error.exception.EmailSendFailException;

import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String ENCODING_UTF8 = "UTF-8";

    private final JavaMailSender javaMailSender;

    @Async
    public void sendHtmlTextEmail(String subject, String content, String email) {
        final MimeMessage message = javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, ENCODING_UTF8);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendFailException();
        }
    }
}
