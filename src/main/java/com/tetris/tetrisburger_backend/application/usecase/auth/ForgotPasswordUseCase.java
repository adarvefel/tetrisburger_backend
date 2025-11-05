package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ForgotPassword;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ForgotPasswordCommand;
import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import com.tetris.tetrisburger_backend.domain.port.out.TokenPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ForgotPasswordUseCase implements ForgotPassword {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordUseCase.class);
    private static final long ONE_HOUR_IN_MILLIS = 3600000L;

    private final UserRepository userRepository;
    private final EmailPort emailPort;
    private final TokenPort tokenPort;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public ForgotPasswordUseCase(
            UserRepository userRepository,
            EmailPort emailPort,
            TokenPort tokenPort) {
        this.userRepository = userRepository;
        this.emailPort = emailPort;
        this.tokenPort = tokenPort;
    }

    /**
     * Genera un token de recuperación de contraseña y envía email al usuario.
     *
     * @param command Comando con el email del usuario
     * @return Email del usuario a quien se envió el token
     * @throws UserNotFoundException Si el usuario no existe
     */
    @Override
    public String execute(ForgotPasswordCommand command) {

        User user = userRepository.findUserByEmail(command.email())
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado para reset: {}", command.email());
                    return new UserNotFoundException("Usuario no encontrado");
                });

        // Generar token de reset
        String resetToken = tokenPort.generatePasswordResetToken(
                command.email(),
                ONE_HOUR_IN_MILLIS
        );

        // Construir email con URL configurable
        String resetUrl = String.format("%s/reset-password?token=%s", frontendUrl, resetToken);
        String emailBody = String.format(
                "Hola %s,\n\n" +
                        "Hemos recibido una solicitud para recuperar tu contraseña.\n\n" +
                        "Usa este enlace para resetearla:\n" +
                        "%s\n\n" +
                        "Este enlace expira en 1 hora.\n\n" +
                        "Si no solicitaste esto, ignora este correo.",
                user.getUserName(),
                resetUrl
        );

        // Enviar email
        emailPort.sendEmail(
                command.email(),
                "Recuperación de contraseña - TetrisBurger",
                emailBody
        );

        logger.info("Email de recuperación enviado a: {}", command.email());

        // Retornar el email para que el controller pueda usarlo en el response
        return command.email();
    }
}
