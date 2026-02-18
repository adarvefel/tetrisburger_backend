package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.exception.InvalidCredentialsException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidRecaptchaException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginUser;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
import com.tetris.tetrisburger_backend.domain.port.out.TokenPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LoginUserUseCase implements LoginUser {

    private static final Logger logger = LoggerFactory.getLogger(LoginUserUseCase.class);

    private final UserRepository userRepository;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;
    private final RecaptchaPort recaptchaPort;


    public LoginUserUseCase(UserRepository userRepository, TokenPort tokenPort, PasswordEncoder passwordEncoder, RecaptchaPort recaptchaPort) {
        this.userRepository = userRepository;
        this.tokenPort = tokenPort;
        this.passwordEncoder = passwordEncoder;
        this.recaptchaPort = recaptchaPort;
    }

    @Override
    public LoginResponse execute(LoginUserCommand command) {
        boolean isHuman = recaptchaPort.verifyToken(command.recaptchaToken(),"login");
        if(!isHuman){
            logger.warn("reCAPTCHA fallo para :{}",command.email());
            throw new InvalidRecaptchaException("  \"Verificación de seguridad falló. Por favor intenta de nuevo.");
        }
        logger.info(" reCAPTCHA verificado para: {}", command.email());


        logger.info("Intento de autenticación para: {}", command.email());

        // 1. Validar correo
        User user = userRepository.findUserByEmail(command.email())
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado: {}", command.email());
                    return new InvalidCredentialsException("Credenciales inválidas");
                });

        // 2. Validar contraseña
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            logger.warn("Contraseña incorrecta para: {}", command.email());
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        // 3. Generar token JWT
        String token = tokenPort.generateToken(user.getEmail());
        long expirationTime = tokenPort.getExpirationTime();

        // 4. Crear LoginResponse (record inmutable)
        LoginResponse response = new LoginResponse(token, user, expirationTime);

        logger.info("Autenticación exitosa para: {}", command.email());
        return response;
    }
}
