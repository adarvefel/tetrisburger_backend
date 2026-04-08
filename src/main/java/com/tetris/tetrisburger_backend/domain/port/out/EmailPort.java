package com.tetris.tetrisburger_backend.domain.port.out;

import java.util.Map;

public interface EmailPort {

    void sendEmail(String to, String subjecy,String body);

    void sendEmail(String to, String subject, String template, Map<String, Object> templateVars);
    void sendWelcomeEmail(String email, String userName);

    void sendPasswordResetEmail(String email, String userName, String resetLink);
}