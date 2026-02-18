package com.tetris.tetrisburger_backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas del Modelo de Dominio User")
class UserTest {

    // ========================================
    // FACTORY METHODS - createByAdmin
    // ========================================

    @Nested
    @DisplayName("Método Factory: createByAdmin")
    class CreateByAdminTests {

        @Test
        @DisplayName("debería crear usuario por admin con todos los campos")
        void shouldCreateUserByAdminWithAllFields() {
            // Given
            String userName = "Juan Pérez";
            String email = "juan.perez@example.com";
            String hashedPassword = "$2a$10$hashedpassword123";
            Role role = Role.ADMIN;
            String phone = "+57 300 1234567";
            String imageKey = "users/juan-123.jpg";
            String imageName = "juan-profile.jpg";
            Integer createdBy = 1;

            // When
            User user = User.createByAdmin(
                    userName, email, hashedPassword, role, phone,
                    imageKey, imageName, createdBy
            );

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getUserName()).isEqualTo("Juan Pérez");
            assertThat(user.getEmail()).isEqualTo("juan.perez@example.com");
            assertThat(user.getPassword()).isEqualTo(hashedPassword);
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
            assertThat(user.getPhone()).isEqualTo("+57 300 1234567");
            assertThat(user.getUserImageKey()).isEqualTo("users/juan-123.jpg");
            assertThat(user.getUserImage()).isEqualTo("juan-profile.jpg");
            assertThat(user.getCreatedBy()).isEqualTo(1);
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNull();
            assertThat(user.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("debería asignar rol CLIENT por defecto cuando role es null")
        void shouldDefaultToClientRoleWhenRoleIsNull() {
            // When
            User user = User.createByAdmin(
                    "María García",
                    "maria@example.com",
                    "$2a$10$hashed",
                    null,
                    null,
                    null,
                    null,
                    1
            );

            // Then
            assertThat(user.getRole()).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("debería crear usuario sin campos opcionales")
        void shouldCreateUserWithoutOptionalFields() {
            // When
            User user = User.createByAdmin(
                    "Carlos López",
                    "carlos@example.com",
                    "$2a$10$hashed",
                    Role.CLIENT,
                    null,
                    null,
                    null,
                    1
            );

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getPhone()).isNull();
            assertThat(user.getUserImageKey()).isNull();
            assertThat(user.getUserImage()).isNull();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("debería lanzar excepción cuando userName es nulo o vacío")
        void shouldThrowExceptionWhenUserNameIsNullOrBlank(String invalidUserName) {
            // Then
            assertThatThrownBy(() ->
                    User.createByAdmin(
                            invalidUserName,
                            "test@example.com",
                            "$2a$10$hashed",
                            Role.CLIENT,
                            null,
                            null,
                            null,
                            1
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El nombre del usuario es requerido");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando userName es demasiado corto")
        void shouldThrowExceptionWhenUserNameIsTooShort() {
            // Then
            assertThatThrownBy(() ->
                    User.createByAdmin(
                            "AB",
                            "test@example.com",
                            "$2a$10$hashed",
                            Role.CLIENT,
                            null,
                            null,
                            null,
                            1
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El nombre debe tener entre 3 y 50 caracteres");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando userName supera 50 caracteres")
        void shouldThrowExceptionWhenUserNameIsTooLong() {
            // Given
            String longName = "a".repeat(51);

            // Then
            assertThatThrownBy(() ->
                    User.createByAdmin(
                            longName,
                            "test@example.com",
                            "$2a$10$hashed",
                            Role.CLIENT,
                            null,
                            null,
                            null,
                            1
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El nombre debe tener entre 3 y 50 caracteres");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("debería lanzar excepción cuando email es nulo o vacío")
        void shouldThrowExceptionWhenEmailIsNullOrBlank(String invalidEmail) {
            // Then
            assertThatThrownBy(() ->
                    User.createByAdmin(
                            "Juan Pérez",
                            invalidEmail,
                            "$2a$10$hashed",
                            Role.CLIENT,
                            null,
                            null,
                            null,
                            1
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El email es requerido");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando email no tiene @")
        void shouldThrowExceptionWhenEmailHasNoAt() {
            // Then
            assertThatThrownBy(() ->
                    User.createByAdmin(
                            "Juan Pérez",
                            "invalidemail",
                            "$2a$10$hashed",
                            Role.CLIENT,
                            null,
                            null,
                            null,
                            1
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Formato de email inválido");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando email no tiene punto después de @")
        void shouldThrowExceptionWhenEmailHasNoDotAfterAt() {
            // Then
            assertThatThrownBy(() ->
                    User.createByAdmin(
                            "Juan Pérez",
                            "user@domain",
                            "$2a$10$hashed",
                            Role.CLIENT,
                            null,
                            null,
                            null,
                            1
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Formato de email inválido");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("debería lanzar excepción cuando contraseña hasheada es nula o vacía")
        void shouldThrowExceptionWhenHashedPasswordIsNullOrBlank(String invalidPassword) {
            // Then
            assertThatThrownBy(() ->
                    User.createByAdmin(
                            "Juan Pérez",
                            "juan@example.com",
                            invalidPassword,
                            Role.CLIENT,
                            null,
                            null,
                            null,
                            1
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La contraseña hasheada es requerida");
        }
    }

    // ========================================
    // FACTORY METHODS - createClient
    // ========================================

    @Nested
    @DisplayName("Método Factory: createClient")
    class CreateClientTests {

        @Test
        @DisplayName("debería crear usuario cliente exitosamente")
        void shouldCreateClientUserSuccessfully() {
            // Given
            String userName = "Pedro Martínez";
            String email = "pedro@example.com";
            String hashedPassword = "$2a$10$hashedpassword";

            // When
            User user = User.createClient(userName, email, hashedPassword);

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getUserName()).isEqualTo("Pedro Martínez");
            assertThat(user.getEmail()).isEqualTo("pedro@example.com");
            assertThat(user.getPassword()).isEqualTo(hashedPassword);
            assertThat(user.getRole()).isEqualTo(Role.CLIENT);
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getPhone()).isNull();
            assertThat(user.getUserImageKey()).isNull();
            assertThat(user.getCreatedBy()).isNull();
        }

        @Test
        @DisplayName("debería lanzar excepción por userName inválido")
        void shouldThrowExceptionForInvalidUserName() {
            // Then
            assertThatThrownBy(() ->
                    User.createClient("AB", "test@example.com", "$2a$10$hashed")
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El nombre debe tener entre 3 y 50 caracteres");
        }

        @Test
        @DisplayName("debería lanzar excepción por email sin @")
        void shouldThrowExceptionForEmailWithoutAt() {
            // Then
            assertThatThrownBy(() ->
                    User.createClient("Juan Pérez", "invalid-email", "$2a$10$hashed")
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Formato de email inválido");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando contraseña está vacía")
        void shouldThrowExceptionWhenPasswordIsBlank() {
            // Then
            assertThatThrownBy(() ->
                    User.createClient("Juan Pérez", "juan@example.com", "")
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La contraseña hasheada es requerida");
        }
    }

    // ========================================
    // BUSINESS METHODS - Image Management
    // ========================================

    @Nested
    @DisplayName("Gestión de Imágenes")
    class ImageManagementTests {

        @Test
        @DisplayName("debería actualizar imagen de usuario exitosamente")
        void shouldUpdateUserImageSuccessfully() {
            // Given
            User user = User.createClient(
                    "Ana Torres",
                    "ana@example.com",
                    "$2a$10$hashed"
            );
            LocalDateTime beforeUpdate = LocalDateTime.now();

            // When
            user.updateImage("users/ana-new.jpg", "ana-profile-new.jpg", 1);

            // Then
            assertThat(user.getUserImageKey()).isEqualTo("users/ana-new.jpg");
            assertThat(user.getUserImage()).isEqualTo("ana-profile-new.jpg");
            assertThat(user.getUpdatedBy()).isEqualTo(1);
            assertThat(user.getUpdatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
        }

        @Test
        @DisplayName("debería remover imagen de usuario exitosamente")
        void shouldRemoveUserImageSuccessfully() {
            // Given
            User user = User.createByAdmin(
                    "Luis Gómez",
                    "luis@example.com",
                    "$2a$10$hashed",
                    Role.CLIENT,
                    null,
                    "users/luis-old.jpg",
                    "luis-old.jpg",
                    1
            );

            // When
            user.removeImage(1);

            // Then
            assertThat(user.getUserImageKey()).isNull();
            assertThat(user.getUserImage()).isNull();
            assertThat(user.getUpdatedBy()).isEqualTo(1);
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("debería actualizar imagen múltiples veces")
        void shouldUpdateImageMultipleTimes() throws InterruptedException {  // ✅ Agregado throws
            // Given
            User user = User.createClient("Test User", "test@example.com", "$2a$10$hashed");

            // When
            user.updateImage("image1.jpg", "name1.jpg", 1);
            LocalDateTime firstUpdate = user.getUpdatedAt();

            Thread.sleep(10);  // ✅ AGREGADO - Espera 10ms

            user.updateImage("image2.jpg", "name2.jpg", 2);

            // Then
            assertThat(user.getUserImageKey()).isEqualTo("image2.jpg");
            assertThat(user.getUserImage()).isEqualTo("name2.jpg");
            assertThat(user.getUpdatedBy()).isEqualTo(2);
            assertThat(user.getUpdatedAt()).isAfter(firstUpdate);
        }
    }

    // ========================================
    // BUSINESS METHODS - Profile Update
    // ========================================

    @Nested
    @DisplayName("Actualización de Perfil")
    class ProfileUpdateTests {

        @Test
        @DisplayName("debería actualizar perfil con nombre y teléfono")
        void shouldUpdateProfileWithUserNameAndPhone() {
            // Given
            User user = User.createClient(
                    "Carlos Ruiz",
                    "carlos@example.com",
                    "$2a$10$hashed"
            );

            // When
            user.updateProfile("Carlos Ruiz Actualizado", "+57 310 9876543", 1);

            // Then
            assertThat(user.getUserName()).isEqualTo("Carlos Ruiz Actualizado");
            assertThat(user.getPhone()).isEqualTo("+57 310 9876543");
            assertThat(user.getUpdatedBy()).isEqualTo(1);
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("debería actualizar solo userName cuando teléfono es nulo")
        void shouldUpdateOnlyUserNameWhenPhoneIsNull() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");

            // When
            user.updateProfile("Nuevo Nombre", null, 1);

            // Then
            assertThat(user.getUserName()).isEqualTo("Nuevo Nombre");
            assertThat(user.getPhone()).isNull();
        }

        @Test
        @DisplayName("debería actualizar solo teléfono cuando userName es nulo")
        void shouldUpdateOnlyPhoneWhenUserNameIsNull() {
            // Given
            User user = User.createClient("Original", "test@example.com", "$2a$10$hashed");

            // When
            user.updateProfile(null, "+57 300 1111111", 1);

            // Then
            assertThat(user.getUserName()).isEqualTo("Original");
            assertThat(user.getPhone()).isEqualTo("+57 300 1111111");
        }

        @Test
        @DisplayName("no debería actualizar cuando userName está vacío")
        void shouldNotUpdateWhenUserNameIsBlank() {
            // Given
            User user = User.createClient("Original", "test@example.com", "$2a$10$hashed");

            // When
            user.updateProfile("   ", "+57 300 1111111", 1);

            // Then
            assertThat(user.getUserName()).isEqualTo("Original");
            assertThat(user.getPhone()).isEqualTo("+57 300 1111111");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando nuevo userName es demasiado corto")
        void shouldThrowExceptionWhenNewUserNameIsTooShort() {
            // Given
            User user = User.createClient("Original", "test@example.com", "$2a$10$hashed");

            // Then
            assertThatThrownBy(() ->
                    user.updateProfile("AB", null, 1)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El nombre debe tener entre 3 y 50 caracteres");
        }
    }

    // ========================================
    // BUSINESS METHODS - updateByAdmin
    // ========================================

    @Nested
    @DisplayName("Actualización por Administrador")
    class UpdateByAdminTests {

        @Test
        @DisplayName("debería actualizar todos los campos por admin")
        void shouldUpdateAllFieldsByAdmin() {
            // Given
            User user = User.createClient(
                    "Usuario Original",
                    "original@example.com",
                    "$2a$10$hashed"
            );

            // When
            user.updateByAdmin(
                    "Usuario Actualizado",
                    "nuevo@example.com",
                    Role.ADMIN,
                    "+57 320 5555555",
                    1
            );

            // Then
            assertThat(user.getUserName()).isEqualTo("Usuario Actualizado");
            assertThat(user.getEmail()).isEqualTo("nuevo@example.com");
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
            assertThat(user.getPhone()).isEqualTo("+57 320 5555555");
            assertThat(user.getUpdatedBy()).isEqualTo(1);
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("debería actualizar solo campos no nulos")
        void shouldUpdateOnlyNonNullFields() {
            // Given
            User user = User.createByAdmin(
                    "Original",
                    "original@example.com",
                    "$2a$10$hashed",
                    Role.CLIENT,
                    "+57 300 1234567",
                    null,
                    null,
                    1
            );

            // When
            user.updateByAdmin(null, null, Role.ADMIN, null, 1);

            // Then
            assertThat(user.getUserName()).isEqualTo("Original");
            assertThat(user.getEmail()).isEqualTo("original@example.com");
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
            assertThat(user.getPhone()).isEqualTo("+57 300 1234567");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando formato de nuevo email es inválido")
        void shouldThrowExceptionWhenNewEmailFormatIsInvalid() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");

            // Then
            assertThatThrownBy(() ->
                    user.updateByAdmin(null, "invalid-email", null, null, 1)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Formato de email inválido");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando nuevo userName es demasiado largo")
        void shouldThrowExceptionWhenNewUserNameIsTooLong() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");
            String longName = "a".repeat(51);

            // Then
            assertThatThrownBy(() ->
                    user.updateByAdmin(longName, null, null, null, 1)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El nombre debe tener entre 3 y 50 caracteres");
        }

        @Test
        @DisplayName("no debería actualizar cuando userName está vacío")
        void shouldNotUpdateWhenUserNameIsBlank() {
            // Given
            User user = User.createClient("Original", "test@example.com", "$2a$10$hashed");

            // When
            user.updateByAdmin("   ", null, null, null, 1);

            // Then
            assertThat(user.getUserName()).isEqualTo("Original");
        }

        @Test
        @DisplayName("no debería actualizar cuando email está vacío")
        void shouldNotUpdateWhenEmailIsBlank() {
            // Given
            User user = User.createClient("Test", "original@example.com", "$2a$10$hashed");

            // When
            user.updateByAdmin(null, "   ", null, null, 1);

            // Then
            assertThat(user.getEmail()).isEqualTo("original@example.com");
        }
    }

    // ========================================
    // BUSINESS METHODS - Password Reset
    // ========================================

    @Nested
    @DisplayName("Restablecimiento de Contraseña")
    class PasswordResetTests {

        @Test
        @DisplayName("debería restablecer contraseña exitosamente")
        void shouldResetPasswordSuccessfully() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$old");
            String newHashedPassword = "$2a$10$newpassword";

            // When
            user.resetPassword(newHashedPassword, 1);

            // Then
            assertThat(user.getPassword()).isEqualTo(newHashedPassword);
            assertThat(user.getUpdatedBy()).isEqualTo(1);
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("debería lanzar excepción cuando nueva contraseña es nula")
        void shouldThrowExceptionWhenNewPasswordIsNull() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$old");

            // Then
            assertThatThrownBy(() ->
                    user.resetPassword(null, 1)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La contraseña hasheada es requerida");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("debería lanzar excepción cuando nueva contraseña está vacía")
        void shouldThrowExceptionWhenNewPasswordIsBlank(String invalidPassword) {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$old");

            // Then
            assertThatThrownBy(() ->
                    user.resetPassword(invalidPassword, 1)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La contraseña hasheada es requerida");
        }
    }

    // ========================================
    // BUSINESS METHODS - Soft Delete
    // ========================================

    @Nested
    @DisplayName("Eliminación Lógica (Soft Delete)")
    class SoftDeleteTests {

        @Test
        @DisplayName("debería marcar usuario como eliminado")
        void shouldMarkUserAsDeleted() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");
            LocalDateTime beforeDelete = LocalDateTime.now();

            // When
            user.markAsDeleted(1);

            // Then
            assertThat(user.getDeletedAt()).isNotNull();
            assertThat(user.getDeletedAt()).isAfterOrEqualTo(beforeDelete);
            assertThat(user.getDeletedBy()).isEqualTo(1);
            assertThat(user.isActive()).isFalse();
        }

        @Test
        @DisplayName("no debería estar activo después de eliminación")
        void shouldNotBeActiveAfterDeletion() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");
            assertThat(user.isActive()).isTrue();

            // When
            user.markAsDeleted(1);

            // Then
            assertThat(user.isActive()).isFalse();
        }
    }

    // ========================================
    // BUSINESS METHODS - Status Checks
    // ========================================

    @Nested
    @DisplayName("Verificación de Estados")
    class StatusChecksTests {

        @Test
        @DisplayName("debería estar activo cuando deletedAt es nulo")
        void shouldBeActiveWhenDeletedAtIsNull() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");

            // Then
            assertThat(user.isActive()).isTrue();
        }

        @Test
        @DisplayName("no debería estar activo cuando deletedAt está definido")
        void shouldNotBeActiveWhenDeletedAtIsSet() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");
            user.markAsDeleted(1);

            // Then
            assertThat(user.isActive()).isFalse();
        }

        @Test
        @DisplayName("debería ser administrativo cuando rol es ADMIN")
        void shouldBeAdministrativeWhenRoleIsAdmin() {
            // Given
            User user = User.createByAdmin(
                    "Admin",
                    "admin@example.com",
                    "$2a$10$hashed",
                    Role.ADMIN,
                    null,
                    null,
                    null,
                    1
            );

            // Then
            assertThat(user.isAdministrative()).isTrue();
        }

        @Test
        @DisplayName("no debería ser administrativo cuando rol es CLIENT")
        void shouldNotBeAdministrativeWhenRoleIsClient() {
            // Given
            User user = User.createClient("Client", "client@example.com", "$2a$10$hashed");

            // Then
            assertThat(user.isAdministrative()).isFalse();
        }
    }

    // ========================================
    // BUSINESS METHODS - Permissions
    // ========================================

    @Nested
    @DisplayName("Permisos: canEdit")
    class PermissionsCanEditTests {

        @Test
        @DisplayName("ADMIN puede editar CLIENT")
        void adminCanEditClient() {
            // Given
            User admin = User.createByAdmin(
                    "Admin", "admin@example.com", "$2a$10$hashed",
                    Role.ADMIN, null, null, null, 1
            );
            User client = User.createClient("Client", "client@example.com", "$2a$10$hashed");

            // Then
            assertThat(admin.canEdit(client)).isTrue();
        }

        @Test
        @DisplayName("ADMIN puede editar otro ADMIN del mismo nivel")
        void adminCanEditSameLevelAdmin() {
            // Given
            User admin1 = User.createByAdmin(
                    "Admin1", "admin1@example.com", "$2a$10$hashed",
                    Role.ADMIN, null, null, null, 1
            );
            User admin2 = User.createByAdmin(
                    "Admin2", "admin2@example.com", "$2a$10$hashed",
                    Role.ADMIN, null, null, null, 1
            );

            // Then
            assertThat(admin1.canEdit(admin2)).isTrue();
        }

        @Test
        @DisplayName("CLIENT no puede editar ADMIN")
        void clientCannotEditAdmin() {
            // Given
            User client = User.createClient("Client", "client@example.com", "$2a$10$hashed");
            User admin = User.createByAdmin(
                    "Admin", "admin@example.com", "$2a$10$hashed",
                    Role.ADMIN, null, null, null, 1
            );

            // Then
            assertThat(client.canEdit(admin)).isFalse();
        }

        @Test
        @DisplayName("CLIENT puede editar otro CLIENT del mismo nivel")
        void clientCanEditSameLevelClient() {
            // Given
            User client1 = User.createClient("Client1", "client1@example.com", "$2a$10$hashed");
            User client2 = User.createClient("Client2", "client2@example.com", "$2a$10$hashed");

            // Then
            assertThat(client1.canEdit(client2)).isTrue();
        }

        @Test
        @DisplayName("debería retornar falso cuando rol de este usuario es nulo")
        void shouldReturnFalseWhenThisUserRoleIsNull() {
            // Given
            User user = new User(
                    1, "Test", "test@example.com", "$2a$10$hashed",
                    null, null, null, null,
                    LocalDateTime.now(), null, null, null, null, null
            );
            User otherUser = User.createClient("Other", "other@example.com", "$2a$10$hashed");

            // Then
            assertThat(user.canEdit(otherUser)).isFalse();
        }

        @Test
        @DisplayName("debería retornar falso cuando rol del otro usuario es nulo")
        void shouldReturnFalseWhenOtherUserRoleIsNull() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");
            User otherUser = new User(
                    1, "Other", "other@example.com", "$2a$10$hashed",
                    null, null, null, null,
                    LocalDateTime.now(), null, null, null, null, null
            );

            // Then
            assertThat(user.canEdit(otherUser)).isFalse();
        }
    }

    // ========================================
    // CONSTRUCTOR AND GETTERS
    // ========================================

    @Nested
    @DisplayName("Constructor y Getters")
    class ConstructorAndGettersTests {

        @Test
        @DisplayName("debería crear usuario con constructor completo")
        void shouldCreateUserWithFullConstructor() {
            // Given
            LocalDateTime now = LocalDateTime.now();

            // When
            User user = new User(
                    1,
                    "Test User",
                    "test@example.com",
                    "$2a$10$hashed",
                    "profile.jpg",
                    "users/profile.jpg",
                    Role.CLIENT,
                    "+57 300 1234567",
                    now,
                    now,
                    null,
                    1,
                    1,
                    null
            );

            // Then
            assertThat(user.getIdUser()).isEqualTo(1);
            assertThat(user.getUserName()).isEqualTo("Test User");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getPassword()).isEqualTo("$2a$10$hashed");
            assertThat(user.getUserImage()).isEqualTo("profile.jpg");
            assertThat(user.getUserImageKey()).isEqualTo("users/profile.jpg");
            assertThat(user.getRole()).isEqualTo(Role.CLIENT);
            assertThat(user.getPhone()).isEqualTo("+57 300 1234567");
            assertThat(user.getCreatedAt()).isEqualTo(now);
            assertThat(user.getUpdatedAt()).isEqualTo(now);
            assertThat(user.getDeletedAt()).isNull();
            assertThat(user.getCreatedBy()).isEqualTo(1);
            assertThat(user.getUpdatedBy()).isEqualTo(1);
            assertThat(user.getDeletedBy()).isNull();
        }

        @Test
        @DisplayName("debería establecer idUser con setter")
        void shouldSetIdUserWithSetter() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");

            // When
            user.setIdUser(999);

            // Then
            assertThat(user.getIdUser()).isEqualTo(999);
        }
    }
}
