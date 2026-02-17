package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.exception.InvalidCredentialsException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginWithGoogle;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginWithGoogleCommand;
import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import com.tetris.tetrisburger_backend.domain.port.out.GoogleAuthPort;
import com.tetris.tetrisburger_backend.domain.port.out.TokenPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class LoginWithGoogleUseCase implements LoginWithGoogle {

    private static final Logger logger = LoggerFactory.getLogger(LoginWithGoogleUseCase.class);

    private final UserRepository userRepository;
    private final GoogleAuthPort googleAuthPort;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;
    private final EmailPort emailPort;

    public LoginWithGoogleUseCase(
            UserRepository userRepository,
            GoogleAuthPort googleAuthPort,
            TokenPort tokenPort,
            PasswordEncoder passwordEncoder,
            EmailPort emailPort) {
        this.userRepository = userRepository;
        this.googleAuthPort = googleAuthPort;
        this.tokenPort = tokenPort;
        this.passwordEncoder = passwordEncoder;
        this.emailPort = emailPort;
    }

    @Override
    public LoginResponse handle(LoginWithGoogleCommand command) {
        logger.info("Intento de autenticación con Google");

        // 1. Validar token de Google
        Map<String, String> userInfo;
        try {
            userInfo = googleAuthPort.validateAndExtractUserInfo(command.googleToken());
        } catch (InvalidTokenException e) {
            logger.error("Error validando token de Google: {}", e.getMessage());
            throw new InvalidCredentialsException("Autenticación con Google fallida");
        }

        // 2. Extraer información
        String email = userInfo.get("email");
        String userName = userInfo.get("userName");

        // 3. Buscar o crear usuario
        User user = userRepository.findUserByEmail(email)
                .orElseGet(() -> createNewGoogleUser(email, userName));

        // 4. Generar token JWT
        String token = tokenPort.generateToken(user.getEmail());
        long expirationTime = tokenPort.getExpirationTime();

        // 5. Crear LoginResponse directamente (record)
        LoginResponse response = new LoginResponse(token, user, expirationTime);

        logger.info("Autenticación con Google exitosa para: {}", email);
        return response;
    }

    /**
     * Crear nuevo usuario desde información de Google
     */
    private User createNewGoogleUser(String email, String userName) {
        logger.info("Creando nuevo usuario desde Google: {}", email);

        // Generar contraseña aleatoria (no será usada en Google OAuth)
        String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        // Usar factory method del dominio
        User newUser = User.createClient(
                userName,
                email,
                randomPassword  // Ya está hasheada
                            // phone es null (Google no lo proporciona)
        );

        // Guardar usuario
        User savedUser = userRepository.saveUser(newUser);

        // Enviar email de bienvenida (no debe romper el registro si falla)
        try {
            emailPort.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUserName());
            logger.info("Email de bienvenida enviado a: {}", savedUser.getEmail());
        } catch (Exception e) {
            logger.error("Error al enviar email de bienvenida a {}: {}",
                    savedUser.getEmail(), e.getMessage());
        }

        logger.info("Usuario creado desde Google con ID: {}", savedUser.getIdUser());
        return savedUser;
    }
}
