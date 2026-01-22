package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Adaptador para envío de emails usando SMTP.
 * Métodos @Async para no bloquear el flujo principal.
 * Requiere @EnableAsync en la clase principal.
 */
@Component
public class EmailAdapter implements EmailPort {

    private static final Logger logger = LoggerFactory.getLogger(EmailAdapter.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:tetrisburger8@gmail.com}")
    private String fromEmail;

    public EmailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async  // Envío asíncrono (no bloquea el registro)
    public void sendEmail(String to, String subject, String body) {
        logger.info("Enviando email a {} con asunto: {}", to, subject);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Email enviado exitosamente a: {}", to);
        } catch (MailException e) {
            logger.error(" Error enviando email a {}: {}", to, e.getMessage());
            // No rethrow: Continúa el flujo (fail-safe)
        }
    }

    @Override
    @Async
    public void sendEmail(String to, String subject, String template, Map<String, Object> templateVars) {
        String body = renderTemplate(template, templateVars);
        sendEmail(to, subject, body);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String userName) {
        logger.info("Preparando email de bienvenida para: {}", to);

        String subject = "¡Bienvenido a TetrisBurger! 🍔kw0";
        String body = String.format(
                "Hola %s,\n\n" +
                        "¡Gracias por registrarte en TetrisBurger!\n\n" +
                        "Tu cuenta ha sido creada exitosamente.\n" +
                        "Ahora puedes disfrutar de nuestras deliciosas hamburguesas.\n\n" +
                        "Si tienes alguna pregunta, contáctanos en support@tetrisburger.com\n\n" +
                        "¡Que disfrutes!\n\n" +
                        "El equipo de TetrisBurger",
                userName
        );

        sendEmail(to, subject, body);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String userName, String resetLink) {
        logger.info("Preparando email de recuperación para: {}", to);

        String subject = "Recuperación de Contraseña - TetrisBurger";
        String body = String.format(
                "Hola %s,\n\n" +
                        "Recibimos una solicitud para recuperar tu contraseña.\n\n" +
                        "Usa este enlace seguro para restablecerla:\n" +
                        "%s\n\n" +
                        "  Este enlace expira en 1 hora.\n" +
                        "Si no solicitaste esto, ignora este mensaje.\n\n" +
                        "Saludos,\n" +
                        "El equipo de TetrisBurger",
                userName,
                resetLink
        );

        sendEmail(to, subject, body);
    }

    /**
     * Renderiza una plantilla simple reemplazando {variables}
     */
    private String renderTemplate(String template, Map<String, Object> vars) {
        if (vars == null || vars.isEmpty()) {
            return template;
        }

        String body = template;
        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            body = body.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return body;
    }
}
