package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ForgotPasswordCommand;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginWithGoogleCommand;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ResetPasswordCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.RegisterUserCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.RegisterUserRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.RegisterUserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

/**
 * Mapper para DTOs de autenticación ↔ Commands
 */
@Mapper(componentModel = "spring")
public interface AuthRestDtoMapper {

    // ============================================
    // DTO → COMMAND (ENTRADA)
    // ============================================

    /**
     * Mapea RegisterUserRequestDTO → RegisterUserCommand
     */
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    RegisterUserCommand toRegisterCommand(RegisterUserRequestDTO dto);

    /**
     * Mapea LoginRequestDTO → LoginUserCommand
     */
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    LoginUserCommand toLoginCommand(LoginRequestDTO dto);

    /**
     * Mapea GoogleLoginRequestDTO → LoginWithGoogleCommand
     */
    @Mapping(source = "token", target = "googleToken")
    LoginWithGoogleCommand toLoginWithGoogleCommand(GoogleLoginRequestDTO dto);

    /**
     * Mapea ForgotPasswordRequestDTO → ForgotPasswordCommand
     */
    @Mapping(source = "email", target = "email")
    @Mapping(source = "recaptchaToken", target = "recaptchaToken")

    ForgotPasswordCommand toForgotPasswordCommand(  //Nombre correcto
                                                    ForgotPasswordRequestDTO dto);

    /**
     * Mapea ResetPasswordRequestDTO → ResetPasswordCommand
     */
    @Mapping(source = "token", target = "token")
    @Mapping(source = "newPassword", target = "newPassword")
    ResetPasswordCommand toResetPasswordCommand(ResetPasswordRequestDTO dto);

    // ============================================
    // DOMAIN → RESPONSE DTO (SALIDA)
    // ============================================

    /**
     * Mapea User (domain) → UserInfoDTO (REST)
     */
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userImage",target = "userImage")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UserInfoDTO toUserInfoDTO(User user);

    /**
     * Mapea User (domain) → RegisterUserResponseDTO (REST)
     */
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    RegisterUserResponseDTO toRegisterResponseDTO(User user);

    /**
     * Mapea LoginResponse (domain) → LoginResponseDTO (REST)
     */
    default LoginResponseDTO toLoginResponseDTO(LoginResponse domainResponse) {
        if (domainResponse == null) return null;

        return new LoginResponseDTO(
                domainResponse.token(),
                "Bearer",                           // Token type
                domainResponse.expiresIn(),
                toUserInfoDTO(domainResponse.user()),
                LocalDateTime.now()
        );
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    @Named("roleToString")
    default String roleToString(Role role) {
        if (role == null) return null;
        return role.name();
    }
}
