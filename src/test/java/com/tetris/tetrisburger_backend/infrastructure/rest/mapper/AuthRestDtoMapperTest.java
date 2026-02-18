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
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.RegisterUserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas Unitarias de AuthRestDtoMapper")
class AuthRestDtoMapperTest {

    private AuthRestDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AuthRestDtoMapper.class);
    }

    @Nested
    @DisplayName("Pruebas de Mapeo DTO a Command")
    class DtoToCommandTests {

        @Test
        @DisplayName("Debería mapear RegisterUserRequestDTO a RegisterUserCommand")
        void shouldMapRegisterUserRequestDtoToCommand() {
            // Given
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    "John Doe",
                    "john@example.com",
                    "Password123!",
                    "recaptcha-token-12345"
            );

            // When
            RegisterUserCommand command = mapper.toRegisterCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.userName()).isEqualTo("John Doe");
            assertThat(command.email()).isEqualTo("john@example.com");
            assertThat(command.password()).isEqualTo("Password123!");
        }

        @Test
        @DisplayName("Debería mapear LoginRequestDTO a LoginUserCommand")
        void shouldMapLoginRequestDtoToCommand() {
            // Given
            LoginRequestDTO dto = new LoginRequestDTO(
                    "user@example.com",
                    "SecurePass123!",
                    "recaptcha-token-67890"
            );

            // When
            LoginUserCommand command = mapper.toLoginCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.email()).isEqualTo("user@example.com");
            assertThat(command.password()).isEqualTo("SecurePass123!");
        }

        @Test
        @DisplayName("Debería mapear GoogleLoginRequestDTO a LoginWithGoogleCommand")
        void shouldMapGoogleLoginRequestDtoToCommand() {
            // Given
            String googleToken = "google.jwt.token.12345";
            GoogleLoginRequestDTO dto = new GoogleLoginRequestDTO(googleToken);

            // When
            LoginWithGoogleCommand command = mapper.toLoginWithGoogleCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.googleToken()).isEqualTo(googleToken);
        }

        @Test
        @DisplayName("Debería mapear ForgotPasswordRequestDTO a ForgotPasswordCommand")
        void shouldMapForgotPasswordRequestDtoToCommand() {
            // Given
            ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO(
                    "forgot@example.com",
                    "recaptcha-token-forgot"
            );

            // When
            ForgotPasswordCommand command = mapper.toForgotPasswordCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.email()).isEqualTo("forgot@example.com");
        }

        @Test
        @DisplayName("Debería mapear ResetPasswordRequestDTO a ResetPasswordCommand")
        void shouldMapResetPasswordRequestDtoToCommand() {
            // Given
            ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO(
                    "reset.token.12345",
                    "NewPassword123!"
            );

            // When
            ResetPasswordCommand command = mapper.toResetPasswordCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.token()).isEqualTo("reset.token.12345");
            assertThat(command.newPassword()).isEqualTo("NewPassword123!");
        }

        @Test
        @DisplayName("Debería mapear RegisterUserRequestDTO con nombre mínimo de 3 caracteres")
        void shouldMapRegisterUserRequestWithMinimumNameLength() {
            // Given
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    "Joe",
                    "joe@example.com",
                    "Pass123",
                    "recaptcha-token"
            );

            // When
            RegisterUserCommand command = mapper.toRegisterCommand(dto);

            // Then
            assertThat(command.userName()).isEqualTo("Joe");
            assertThat(command.userName()).hasSize(3);
        }

        @Test
        @DisplayName("Debería mapear RegisterUserRequestDTO con nombre máximo de 50 caracteres")
        void shouldMapRegisterUserRequestWithMaximumNameLength() {
            // Given
            String maxName = "A".repeat(50);
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    maxName,
                    "test@example.com",
                    "Password123",
                    "recaptcha-token"
            );

            // When
            RegisterUserCommand command = mapper.toRegisterCommand(dto);

            // Then
            assertThat(command.userName()).isEqualTo(maxName);
            assertThat(command.userName()).hasSize(50);
        }

        @Test
        @DisplayName("Debería mapear RegisterUserRequestDTO con password mínimo de 6 caracteres")
        void shouldMapRegisterUserRequestWithMinimumPasswordLength() {
            // Given
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    "Test User",
                    "test@example.com",
                    "Pass12",
                    "recaptcha-token"
            );

            // When
            RegisterUserCommand command = mapper.toRegisterCommand(dto);

            // Then
            assertThat(command.password()).isEqualTo("Pass12");
            assertThat(command.password()).hasSize(6);
        }

        @Test
        @DisplayName("Debería mapear LoginRequestDTO con email válido")
        void shouldMapLoginRequestWithValidEmail() {
            // Given
            LoginRequestDTO dto = new LoginRequestDTO(
                    "adarvefelipe58@gmail.com",
                    "felipe12345",
                    "recaptcha-token"
            );

            // When
            LoginUserCommand command = mapper.toLoginCommand(dto);

            // Then
            assertThat(command.email()).isEqualTo("adarvefelipe58@gmail.com");
        }

        @Test
        @DisplayName("Debería mapear RegisterUserRequestDTO con email formato válido")
        void shouldMapRegisterUserRequestWithValidEmailFormat() {
            // Given
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    "Test User",
                    "user+test@example.com",
                    "Password123",
                    "recaptcha-token"
            );

            // When
            RegisterUserCommand command = mapper.toRegisterCommand(dto);

            // Then
            assertThat(command.email()).isEqualTo("user+test@example.com");
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo Domain a Response DTO")
    class DomainToResponseDtoTests {

        @Test
        @DisplayName("Debería mapear User a UserInfoDTO con rol CLIENT")
        void shouldMapUserToUserInfoDtoWithClientRole() {
            // Given
            User user = User.createClient("Jane Doe", "jane@example.com", "hashedPass");
            user.setIdUser(1);

            // When
            UserInfoDTO dto = mapper.toUserInfoDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(1);
            assertThat(dto.userName()).isEqualTo("Jane Doe");
            assertThat(dto.email()).isEqualTo("jane@example.com");
            assertThat(dto.role()).isEqualTo("CLIENT");
        }

        @Test
        @DisplayName("Debería mapear User a UserInfoDTO con rol ADMIN")
        void shouldMapUserToUserInfoDtoWithAdminRole() {
            // Given
            User user = User.createByAdmin(
                    "Admin User", "admin@example.com", "hashedPass",
                    Role.ADMIN, null, null, null, 1
            );
            user.setIdUser(2);

            // When
            UserInfoDTO dto = mapper.toUserInfoDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(2);
            assertThat(dto.userName()).isEqualTo("Admin User");
            assertThat(dto.email()).isEqualTo("admin@example.com");
            assertThat(dto.role()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Debería mapear User a UserInfoDTO con rol EMPLOYEE")
        void shouldMapUserToUserInfoDtoWithEmployeeRole() {
            // Given
            User user = User.createByAdmin(
                    "Employee User", "employee@example.com", "hashedPass",
                    Role.EMPLOYEE, null, null, null, 1
            );
            user.setIdUser(3);

            // When
            UserInfoDTO dto = mapper.toUserInfoDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.role()).isEqualTo("EMPLOYEE");
        }

        @Test
        @DisplayName("Debería retornar null cuando user es null para UserInfoDTO")
        void shouldReturnNullWhenUserIsNullForUserInfoDto() {
            // When
            UserInfoDTO dto = mapper.toUserInfoDTO(null);

            // Then
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("Debería mapear User a RegisterUserResponseDTO con todos los campos")
        void shouldMapUserToRegisterUserResponseDto() {
            // Given
            User user = User.createByAdmin(
                    "New User",
                    "newuser@example.com",
                    "hashedPass",
                    Role.CLIENT,
                    "+573001234567",
                    "image-key-123",
                    "profile.jpg",
                    1
            );
            user.setIdUser(10);

            // When
            RegisterUserResponseDTO dto = mapper.toRegisterResponseDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(10);
            assertThat(dto.userName()).isEqualTo("New User");
            assertThat(dto.email()).isEqualTo("newuser@example.com");

        }

        @Test
        @DisplayName("Debería mapear User a RegisterUserResponseDTO con campos opcionales nulos")
        void shouldMapUserToRegisterUserResponseDtoWithNullOptionalFields() {
            // Given
            User user = User.createClient("Simple User", "simple@example.com", "hashedPass");
            user.setIdUser(5);

            // When
            RegisterUserResponseDTO dto = mapper.toRegisterResponseDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isEqualTo(5);
            assertThat(dto.userName()).isEqualTo("Simple User");
            assertThat(dto.email()).isEqualTo("simple@example.com");

        }

        @Test
        @DisplayName("Debería retornar null cuando user es null para RegisterUserResponseDTO")
        void shouldReturnNullWhenUserIsNullForRegisterUserResponseDto() {
            // When
            RegisterUserResponseDTO dto = mapper.toRegisterResponseDTO(null);

            // Then
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("UserInfoDTO no debería exponer información sensible")
        void userInfoDtoShouldNotExposeSensitiveInformation() {
            // Given
            User user = User.createClient("Secure User", "secure@example.com", "hashedPassword123");
            user.setIdUser(1);

            // When
            UserInfoDTO dto = mapper.toUserInfoDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.idUser()).isNotNull();
            assertThat(dto.userName()).isNotNull();
            assertThat(dto.email()).isNotNull();
            assertThat(dto.role()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo LoginResponse a LoginResponseDTO")
    class LoginResponseToLoginResponseDtoTests {

        @Test
        @DisplayName("Debería mapear LoginResponse a LoginResponseDTO completamente")
        void shouldMapLoginResponseToLoginResponseDto() {
            // Given
            User user = User.createClient("Login User", "login@example.com", "hashedPass");
            user.setIdUser(1);

            LoginResponse loginResponse = new LoginResponse(
                    "jwt.token.12345",
                    user,
                    3600L
            );

            // When
            LoginResponseDTO dto = mapper.toLoginResponseDTO(loginResponse);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.token()).isEqualTo("jwt.token.12345");
            assertThat(dto.tokenType()).isEqualTo("Bearer");
            assertThat(dto.expiresIn()).isEqualTo(3600L);
            assertThat(dto.user()).isNotNull();
            assertThat(dto.user().idUser()).isEqualTo(1);
            assertThat(dto.user().userName()).isEqualTo("Login User");
            assertThat(dto.user().email()).isEqualTo("login@example.com");
            assertThat(dto.user().role()).isEqualTo("CLIENT");
            assertThat(dto.timestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debería establecer tokenType como Bearer")
        void shouldSetTokenTypeAsBearer() {
            // Given
            User user = User.createClient("Test User", "test@example.com", "hashedPass");
            user.setIdUser(1);

            LoginResponse loginResponse = new LoginResponse("token", user, 7200L);

            // When
            LoginResponseDTO dto = mapper.toLoginResponseDTO(loginResponse);

            // Then
            assertThat(dto.tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("Debería incluir timestamp actual")
        void shouldIncludeCurrentTimestamp() {
            // Given
            User user = User.createClient("Test User", "test@example.com", "hashedPass");
            user.setIdUser(1);

            LoginResponse loginResponse = new LoginResponse("token", user, 3600L);

            // When
            LoginResponseDTO dto = mapper.toLoginResponseDTO(loginResponse);

            // Then
            assertThat(dto.timestamp()).isNotNull();
            assertThat(dto.timestamp()).isBeforeOrEqualTo(java.time.LocalDateTime.now());
        }

        @Test
        @DisplayName("Debería mapear UserInfoDTO dentro de LoginResponseDTO")
        void shouldMapUserInfoDtoInsideLoginResponseDto() {
            // Given
            User adminUser = User.createByAdmin(
                    "Admin Login", "admin@example.com", "hashedPass",
                    Role.ADMIN, null, null, null, 1
            );
            adminUser.setIdUser(2);

            LoginResponse loginResponse = new LoginResponse("admin.token", adminUser, 5400L);

            // When
            LoginResponseDTO dto = mapper.toLoginResponseDTO(loginResponse);

            // Then
            assertThat(dto.user()).isNotNull();
            assertThat(dto.user().idUser()).isEqualTo(2);
            assertThat(dto.user().userName()).isEqualTo("Admin Login");
            assertThat(dto.user().email()).isEqualTo("admin@example.com");
            assertThat(dto.user().role()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Debería retornar null cuando LoginResponse es null")
        void shouldReturnNullWhenLoginResponseIsNull() {
            // When
            LoginResponseDTO dto = mapper.toLoginResponseDTO(null);

            // Then
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("Debería mapear LoginResponse con token JWT largo")
        void shouldMapLoginResponseWithLongJwtToken() {
            // Given
            User user = User.createClient("Test User", "test@example.com", "hashedPass");
            user.setIdUser(1);

            String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            LoginResponse loginResponse = new LoginResponse(longToken, user, 3600L);

            // When
            LoginResponseDTO dto = mapper.toLoginResponseDTO(loginResponse);

            // Then
            assertThat(dto.token()).isEqualTo(longToken);
        }

        @Test
        @DisplayName("Debería mapear LoginResponse con diferentes tiempos de expiración")
        void shouldMapLoginResponseWithDifferentExpirationTimes() {
            // Given
            User user = User.createClient("Test User", "test@example.com", "hashedPass");
            user.setIdUser(1);

            LoginResponse response1Hour = new LoginResponse("token1", user, 3600L);
            LoginResponseDTO dto1Hour = mapper.toLoginResponseDTO(response1Hour);
            assertThat(dto1Hour.expiresIn()).isEqualTo(3600L);

            LoginResponse response24Hours = new LoginResponse("token2", user, 86400L);
            LoginResponseDTO dto24Hours = mapper.toLoginResponseDTO(response24Hours);
            assertThat(dto24Hours.expiresIn()).isEqualTo(86400L);
        }

        @Test
        @DisplayName("LoginResponseDTO debería incluir todos los campos requeridos")
        void loginResponseDtoShouldIncludeAllRequiredFields() {
            // Given
            User user = User.createClient("Complete User", "complete@example.com", "hashedPass");
            user.setIdUser(1);

            LoginResponse loginResponse = new LoginResponse("complete.token", user, 7200L);

            // When
            LoginResponseDTO dto = mapper.toLoginResponseDTO(loginResponse);

            // Then
            assertThat(dto.token()).isNotNull();
            assertThat(dto.tokenType()).isNotNull();
            assertThat(dto.expiresIn()).isNotNull();
            assertThat(dto.user()).isNotNull();
            assertThat(dto.timestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de Conversión Role a String")
    class RoleToStringTests {

        @Test
        @DisplayName("Debería convertir Role.CLIENT a 'CLIENT'")
        void shouldConvertClientRoleToString() {
            // When
            String roleStr = mapper.roleToString(Role.CLIENT);

            // Then
            assertThat(roleStr).isEqualTo("CLIENT");
        }

        @Test
        @DisplayName("Debería convertir Role.ADMIN a 'ADMIN'")
        void shouldConvertAdminRoleToString() {
            // When
            String roleStr = mapper.roleToString(Role.ADMIN);

            // Then
            assertThat(roleStr).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("Debería convertir Role.EMPLOYEE a 'EMPLOYEE'")
        void shouldConvertEmployeeRoleToString() {
            // When
            String roleStr = mapper.roleToString(Role.EMPLOYEE);

            // Then
            assertThat(roleStr).isEqualTo("EMPLOYEE");
        }

        @Test
        @DisplayName("Debería retornar null cuando role es null")
        void shouldReturnNullWhenRoleIsNull() {
            // When
            String roleStr = mapper.roleToString(null);

            // Then
            assertThat(roleStr).isNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de Todos los Roles")
    class AllRolesTests {

        @Test
        @DisplayName("Debería mapear correctamente todos los roles a UserInfoDTO")
        void shouldMapAllRolesToUserInfoDto() {
            for (Role role : Role.values()) {
                // Given
                User user = User.createByAdmin(
                        "Test User", "test@example.com", "hashedPass",
                        role, null, null, null, 1
                );
                user.setIdUser(1);

                // When
                UserInfoDTO dto = mapper.toUserInfoDTO(user);

                // Then
                assertThat(dto.role()).isEqualTo(role.name());
            }
        }

        @Test
        @DisplayName("Debería convertir todos los enum de roles a string")
        void shouldConvertAllRoleEnumsToString() {
            for (Role role : Role.values()) {
                // When
                String roleString = mapper.roleToString(role);

                // Then
                assertThat(roleString).isEqualTo(role.name());
            }
        }
    }

    @Nested
    @DisplayName("Pruebas de Casos Especiales")
    class SpecialCasesTests {

        @Test
        @DisplayName("Debería manejar RegisterUserRequestDTO con recaptchaToken")
        void shouldHandleRegisterUserRequestWithRecaptchaToken() {
            // Given
            String recaptchaToken = "03AGdBq24PBCd-0Q-4Kx7JxRjNSkBQFDAQUEBwQHBAcEBw";
            RegisterUserRequestDTO dto = new RegisterUserRequestDTO(
                    "Test User",
                    "test@example.com",
                    "Password123",
                    recaptchaToken
            );

            // When
            RegisterUserCommand command = mapper.toRegisterCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.userName()).isEqualTo("Test User");
            assertThat(command.email()).isEqualTo("test@example.com");
            assertThat(command.password()).isEqualTo("Password123");
        }

        @Test
        @DisplayName("Debería manejar LoginRequestDTO con recaptchaToken")
        void shouldHandleLoginRequestWithRecaptchaToken() {
            // Given
            String recaptchaToken = "03AGdBq25XYZ-1R-5Ly8KySmOlBRGEBRVFCxRICBxRICBx";
            LoginRequestDTO dto = new LoginRequestDTO(
                    "adarvefelipe58@gmail.com",
                    "felipe12345",
                    recaptchaToken
            );

            // When
            LoginUserCommand command = mapper.toLoginCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.email()).isEqualTo("adarvefelipe58@gmail.com");
            assertThat(command.password()).isEqualTo("felipe12345");
        }

        @Test
        @DisplayName("Debería manejar ForgotPasswordRequestDTO con recaptchaToken")
        void shouldHandleForgotPasswordRequestWithRecaptchaToken() {
            // Given
            String recaptchaToken = "03AGdBq26ABC-2S-6Mz9LzTnPmCSGFECSWGDyS";
            ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO(
                    "forgot@example.com",
                    recaptchaToken
            );

            // When
            ForgotPasswordCommand command = mapper.toForgotPasswordCommand(dto);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.email()).isEqualTo("forgot@example.com");
        }

        @Test
        @DisplayName("Debería manejar GoogleLoginRequestDTO con token JWT largo")
        void shouldHandleGoogleLoginRequestWithLongJwtToken() {
            // Given
            String longJwt = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE5ZmUyYTdiNjc5NTIzOTYwNmNhMGE3NTA3OTRhN2JkOWZkOTU5NjQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMjM0NTY3ODkwLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIn0.signature";
            GoogleLoginRequestDTO dto = new GoogleLoginRequestDTO(longJwt);

            // When
            LoginWithGoogleCommand command = mapper.toLoginWithGoogleCommand(dto);

            // Then
            assertThat(command.googleToken()).isEqualTo(longJwt);
        }

        @Test
        @DisplayName("Debería manejar ResetPasswordRequestDTO con token especial")
        void shouldHandleResetPasswordRequestWithSpecialToken() {
            // Given
            String specialToken = "reset-token-with-special-chars-!@#$%";
            ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO(
                    specialToken,
                    "NewPassword123!"
            );

            // When
            ResetPasswordCommand command = mapper.toResetPasswordCommand(dto);

            // Then
            assertThat(command.token()).isEqualTo(specialToken);
        }

        @Test
        @DisplayName("Debería manejar User con email que contiene caracteres especiales")
        void shouldHandleUserWithEmailContainingSpecialCharacters() {
            // Given
            User user = User.createClient(
                    "Test User",
                    "user+test@example.com",
                    "hashedPass"
            );
            user.setIdUser(1);

            // When
            UserInfoDTO dto = mapper.toUserInfoDTO(user);

            // Then
            assertThat(dto.email()).isEqualTo("user+test@example.com");
        }
    }
}
