package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ResetPassword;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ResetPasswordCommand;
import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
import com.tetris.tetrisburger_backend.domain.port.out.TokenPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ResetPasswordUseCase implements ResetPassword {


    private final UserRepository userRepository;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaPort recaptchaPort;

    public ResetPasswordUseCase(UserRepository userRepository, TokenPort tokenPort, PasswordEncoder passwordEncoder, RecaptchaPort recaptchaPort) {
        this.userRepository = userRepository;
        this.tokenPort = tokenPort;
        this.passwordEncoder = passwordEncoder;
        this.recaptchaPort = recaptchaPort;
    }

    @Override
    public void handle(ResetPasswordCommand command) {



        // 1. Validar token de PASSWORD RESET
        if (!tokenPort.validatePasswordResetToken(command.token())) {
            throw new InvalidTokenException("Token inválido o expirado");
        }

        // 2. Extraer email del token
        String email = tokenPort.extractEmailFromPasswordResetToken(command.token());

        // 3. Buscar usuario
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // 4. Actualizar contraseña
        User updatedUser = new User(
                user.getIdUser(),
                user.getUserName(),
                user.getEmail(),
                passwordEncoder.encode(command.newPassword()),
                user.getUserImage(),
                user.getUserImageKey(),
                user.getRole(),
                user.getPhone(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt(),
                user.getCreatedBy(),
                user.getUpdatedBy(),
                user.getDeletedBy()


                );

        userRepository.saveUser(updatedUser);

    }
}
