package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;
import sibModel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class EmailAdapter implements EmailPort {

    @Value("${brevo.api-key}")
    private String brevoApiKey;

    private TransactionalEmailsApi getApi() {
        ApiClient client = Configuration.getDefaultApiClient();
        client.setApiKey(brevoApiKey);
        return new TransactionalEmailsApi();
    }

    @Override
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setName("TetrisBurger");
            sender.setEmail("tetrisburger8@gmail.com");

            SendSmtpEmailTo recipient = new SendSmtpEmailTo();
            recipient.setEmail(to);

            SendSmtpEmail email = new SendSmtpEmail();
            email.setSender(sender);
            email.setTo(Collections.singletonList(recipient));
            email.setSubject(subject);
            email.setTextContent(body);

            getApi().sendTransacEmail(email);
        } catch (Exception e) {
            System.err.println(">>> Error enviando email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmail(String to, String subject, String template, Map<String, Object> templateVars) {
        String rendered = renderTemplate(template, templateVars);
        sendEmail(to, subject, rendered);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String userName) {
        String subject = "¡Bienvenido a TetrisBurger! 🍔";
        String body = String.format(
                "Hola %s,\n\n" +
                        "¡Gracias por registrarte en TetrisBurger!\n\n" +
                        "Tu cuenta ha sido creada exitosamente.\n" +
                        "Ahora puedes disfrutar de nuestras deliciosas hamburguesas.\n\n" +
                        "¡Que disfrutes!\n\n" +
                        "El equipo de TetrisBurger", userName
        );
        sendEmail(to, subject, body);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String userName, String resetLink) {
        String subject = "Recuperación de Contraseña - TetrisBurger";
        String body = String.format(
                "Hola %s,\n\n" +
                        "Recibimos una solicitud para recuperar tu contraseña.\n\n" +
                        "Usa este enlace para restablecerla:\n%s\n\n" +
                        "Este enlace expira en 1 hora.\n" +
                        "Si no solicitaste esto, ignora este mensaje.\n\n" +
                        "El equipo de TetrisBurger", userName, resetLink
        );
        sendEmail(to, subject, body);
    }

    private String renderTemplate(String template, Map<String, Object> vars) {
        if (vars == null || vars.isEmpty()) return template;
        String result = template;
        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}