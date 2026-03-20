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
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.RegisterUserRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.RegisterUserResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AuthRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login, registro, Google OAuth y recuperación de contraseña")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final RegisterUser registerUser;
    private final LoginUser loginUser;
    private final LoginWithGoogle loginWithGoogle;
    private final ForgotPassword forgotPassword;
    private final ResetPassword resetPassword;

    private final UserRestDtoMapper userMapper;
    private final AuthRestDtoMapper authMapper;

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

    @PostMapping("/register")
    @Operation(summary = "Registro", description = "Crea una nueva cuenta de usuario.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "idUser": 6,
                              "userName": "rompecucas",
                              "email": "rompecucas12@gmail.com",
                              "userImage": null,
                              "phone": null,
                              "createdAt": "2025-11-05T15:00:45.2081821"
                            }
                            """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                    {
                      "userName": "felipeSa",
                      "email": "pipe58@gmail.com",
                      "password": "pipe1234"
                    }
                    """)
            )
    )
    public ResponseEntity<RegisterUserResponseDTO> register(
            @Valid @RequestBody RegisterUserRequestDTO requestDTO) {

        logger.info("Registrando nuevo usuario con email: {}", requestDTO.email());

        RegisterUserCommand command = authMapper.toRegisterCommand(requestDTO);
        User registeredUser = registerUser.handle(command);
        RegisterUserResponseDTO responseDTO = authMapper.toRegisterResponseDTO(registeredUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Autentica un usuario y retorna un token JWT.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "token": "eyJhbGciOiJIUzI1NiJ9...",
                              "tokenType": "Bearer",
                              "expiresIn": 3600000,
                              "user": {
                                "idUser": 2,
                                "userName": "felipeSA",
                                "email": "pipe58@gmail.com",
                                "role": "ADMIN"
                              },
                              "timestamp": "2025-11-05T17:04:37.037933"
                            }
                            """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                    {
                      "email": "pipe58@gmail.com",
                      "password": "pipe1234"
                    }
                    """)
            )
    )
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {

        logger.info("Intento de login para: {}", loginDTO.email());

        LoginUserCommand command = authMapper.toLoginCommand(loginDTO);
        LoginResponse domainResponse = loginUser.execute(command);
        LoginResponseDTO responseDTO = authMapper.toLoginResponseDTO(domainResponse);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/google")
    @Operation(summary = "Login con Google", description = "Inicia sesión con Google OAuth.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Token de Google inválido")
    })
    public ResponseEntity<LoginResponseDTO> loginWithGoogleEndpoint(
            @Valid @RequestBody GoogleLoginRequestDTO googleLoginDTO) {

        logger.info("Intento de login con Google");

        LoginWithGoogleCommand command = authMapper.toLoginWithGoogleCommand(googleLoginDTO);
        LoginResponse domainResponse = loginWithGoogle.handle(command);
        LoginResponseDTO responseDTO = authMapper.toLoginResponseDTO(domainResponse);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Recuperar contraseña", description = "Envía un enlace de recuperación al correo del usuario.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Correo de recuperación enviado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "message": "Correo de recuperación enviado",
                              "success": true,
                              "timestamp": 1762381426062
                            }
                            """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Email inválido")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                    {
                      "email": "adarvefelipe58@gmail.com"
                    }
                    """)
            )
    )
    public ResponseEntity<MessageResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO forgotDTO) {

        logger.info("Solicitud de recuperación de contraseña para: {}", forgotDTO.email());

        ForgotPasswordCommand command = authMapper.toForgotPasswordCommand(forgotDTO);
        forgotPassword.execute(command);

        return ResponseEntity.ok(new MessageResponseDTO("Correo de recuperación enviado", true));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset de contraseña", description = "Actualiza la contraseña con el token enviado al correo.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contraseña actualizada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "message": "Contraseña actualizada",
                              "success": true,
                              "timestamp": 1762381521207
                            }
                            """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiJ9...",
                      "newPassword": "felipe12345"
                    }
                    """)
            )
    )
    public ResponseEntity<MessageResponseDTO> resetPasswordEndpoint(
            @Valid @RequestBody ResetPasswordRequestDTO resetDTO) {

        logger.info("Intento de resetear contraseña con token");

        ResetPasswordCommand command = authMapper.toResetPasswordCommand(resetDTO);
        resetPassword.handle(command);

        return ResponseEntity.ok(new MessageResponseDTO("Contraseña actualizada", true));
    }
}
