package com.softmakers.manager_service.service;

import com.softmakers.error.exception.FileConvertFailException;
import com.softmakers.manager_domain.entity.dto.redis.RegisterCode;
import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.RegisterCodeRedisService;
import com.softmakers.utilities.EmailService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailCodeService {

    private static final int REGISTER_CODE_LENGTH = 6;
    private static final String REGISTER_EMAIL_SUBJECT_POSTFIX = ", Welcome to Instagram.";

    private final EmailService emailService;
    private final RegisterCodeRedisService registerCodeRedisService;
    private final ServiceLifecycle serviceLifecycle;

    private String confirmEmailUI;
    private String resetPasswordEmailUI;

    public EmailCodeService(EmailService emailService, ServiceLifecycle serviceLifecycle) {
        this.emailService = emailService;
        this.serviceLifecycle = serviceLifecycle;
        this.registerCodeRedisService = this.serviceLifecycle.requestRegisterCodeRedisService();
    }

    public void sendRegisterCode(String username, String email) {
        final String code = createConfirmationCode(REGISTER_CODE_LENGTH);
        emailService.sendHtmlTextEmail(username + REGISTER_EMAIL_SUBJECT_POSTFIX, getRegisterEmailText(email, code),
                email);

        final RegisterCode registerCode = RegisterCode.builder()
                .username(username)
                .email(email)
                .code(code)
                .build();
        this.registerCodeRedisService.registerCodeRedisSave(registerCode);
    }

    private String createConfirmationCode(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    private String getRegisterEmailText(String email, String code) {
        return String.format(confirmEmailUI, email, code, email);
    }

    @PostConstruct
    private void loadEmailUI() {
        try {
            final ClassPathResource confirmEmailUIResource = new ClassPathResource("confirmEmailUI.html");
            final ClassPathResource resetPasswordEmailUIResource = new ClassPathResource("resetPasswordEmailUI.html");

            confirmEmailUI = new String(confirmEmailUIResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            resetPasswordEmailUI = new String(resetPasswordEmailUIResource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileConvertFailException();
        }
    }
}
