package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ForgotPassword;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginUser;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginWithGoogle;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ResetPassword;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ForgotPasswordCommand;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginWithGoogleCommand;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ResetPasswordCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.RegisterUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.RegisterUserCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.RegisterUserRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.RegisterUserResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AuthRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para autenticación
 * Sigue Clean Architecture: coordina entre REST y casos de uso
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // Ports IN
    private final RegisterUser registerUser;
    private final LoginUser loginUser;
    private final LoginWithGoogle loginWithGoogle;
    private final ForgotPassword forgotPassword;
    private final ResetPassword resetPassword;

    // Mappers
    private final UserRestDtoMapper userMapper;
    private final AuthRestDtoMapper authMapper;  // ✅ Agregar AuthRestDtoMapper

    public AuthController(
            RegisterUser registerUser,
            LoginUser loginUser,
            LoginWithGoogle loginWithGoogle,
            ForgotPassword forgotPassword,
            ResetPassword resetPassword,
            UserRestDtoMapper userMapper,
            AuthRestDtoMapper authMapper) {
        this.registerUser = registerUser;
        this.loginUser = loginUser;
        this.loginWithGoogle = loginWithGoogle;
        this.forgotPassword = forgotPassword;
        this.resetPassword = resetPassword;
        this.userMapper = userMapper;
        this.authMapper = authMapper;
    }

    /**
     * POST /api/auth/register
     * Registrar nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> register(
            @Valid @RequestBody RegisterUserRequestDTO requestDTO) {

        logger.info("Registrando nuevo usuario con email: {}", requestDTO.email());

        // 1. DTO → Command
        RegisterUserCommand command = authMapper.toRegisterCommand(requestDTO);

        // 2. Ejecutar caso de uso
        User registeredUser = registerUser.handle(command);

        // 3. Domain → DTO
        RegisterUserResponseDTO responseDTO = authMapper.toRegisterResponseDTO(registeredUser);

        logger.info("Usuario registrado exitosamente con ID: {}", registeredUser.getIdUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * POST /api/auth/login
     * Iniciar sesión con email y contraseña
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginDTO) {

        logger.info("Intento de login para: {}", loginDTO.email());

        // 1. DTO → Command
        LoginUserCommand command = authMapper.toLoginCommand(loginDTO);

        // 2. Ejecutar caso de uso (retorna LoginResponse del domain)
        LoginResponse domainResponse = loginUser.execute(command);

        // 3. LoginResponse (domain) → LoginResponseDTO (REST)
        LoginResponseDTO responseDTO = authMapper.toLoginResponseDTO(domainResponse);

        logger.info("Login exitoso para: {}", loginDTO.email());
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * POST /api/auth/google
     * Iniciar sesión con Google OAuth
     */
    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> loginWithGoogleEndpoint(
            @Valid @RequestBody GoogleLoginRequestDTO googleLoginDTO) {

        logger.info("Intento de login con Google");

        // 1. DTO → Command (usar mapper)
        LoginWithGoogleCommand command = authMapper.toLoginWithGoogleCommand(googleLoginDTO);  // ✅ CAMBIO: usar mapper

        // 2. Ejecutar caso de uso
        LoginResponse domainResponse = loginWithGoogle.handle(command);
        // 3. Domain → DTO
        LoginResponseDTO responseDTO = authMapper.toLoginResponseDTO(domainResponse);

        logger.info("Login con Google exitoso");
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * POST /api/auth/forgot-password
     * Generar token de recuperación de contraseña
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO forgotDTO) {

        logger.info("Solicitud de recuperación de contraseña para: {}", forgotDTO.email());

        ForgotPasswordCommand command = authMapper.toForgotPasswordCommand(forgotDTO);
        forgotPassword.execute(command);

        logger.info("Token de recuperación generado para: {}", forgotDTO.email());

        // ✅ Usa MessageResponseDTO (reutilizable)
        return ResponseEntity.ok(new MessageResponseDTO(
                "Correo de recuperación enviado",
                true
        ));
    }


    /**
     * POST /api/auth/reset-password
     * Resetear contraseña con token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDTO> resetPasswordEndpoint(
            @Valid @RequestBody ResetPasswordRequestDTO resetDTO) {

        logger.info("Intento de resetear contraseña con token");

        // 1. DTO → Command (usar mapper)
        ResetPasswordCommand command = authMapper.toResetPasswordCommand(resetDTO);  // usar mapper

        // 2. Ejecutar caso de uso
        resetPassword.handle(command);  // ✅ CAMBIO: execute()

        logger.info("Contraseña reseteada exitosamente");
        return ResponseEntity.ok(new MessageResponseDTO(
                "Contraseña actualizada",
                true
        ));
    }
}
