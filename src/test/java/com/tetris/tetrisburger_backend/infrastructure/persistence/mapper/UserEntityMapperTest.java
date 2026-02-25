package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas Unitarias de UserEntityMapper")
class UserEntityMapperTest {

    private UserEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserEntityMapper.class);
    }

    @Nested
    @DisplayName("Pruebas de Mapeo Entity a Domain")
    class EntityToDomainTests {

        @Test
        @DisplayName("Debería mapear UserEntity a User correctamente con todos los campos")
        void shouldMapUserEntityToUserWithAllFields() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            UserEntity entity = new UserEntity();
            entity.setIdUser(1);
            entity.setUserName("John Doe");
            entity.setEmail("john@example.com");
            entity.setPassword("hashedPassword123");
            entity.setUserImage("profile.jpg");
            entity.setUserImageKey("s3-key-123");
            entity.setRole(Role.CLIENT);
            entity.setPhone("+573001234567");
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setDeletedAt(null);
            entity.setCreatedBy(1);
            entity.setUpdatedBy(1);
            entity.setDeletedBy(null);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getIdUser()).isEqualTo(1);
            assertThat(user.getUserName()).isEqualTo("John Doe");
            assertThat(user.getEmail()).isEqualTo("john@example.com");
            assertThat(user.getPassword()).isEqualTo("hashedPassword123");
            assertThat(user.getUserImage()).isEqualTo("profile.jpg");
            assertThat(user.getRole()).isEqualTo(Role.CLIENT);
            assertThat(user.getPhone()).isEqualTo("+573001234567");
            assertThat(user.getCreatedAt()).isEqualTo(now);
            assertThat(user.getUpdatedAt()).isEqualTo(now);
            assertThat(user.getDeletedAt()).isNull();
            assertThat(user.getCreatedBy()).isEqualTo(1);
            assertThat(user.getUpdatedBy()).isEqualTo(1);
            assertThat(user.getDeletedBy()).isNull();
        }

        @Test
        @DisplayName("Debería mapear UserEntity con rol ADMIN")
        void shouldMapUserEntityWithAdminRole() {
            // Given
            UserEntity entity = new UserEntity();
            entity.setIdUser(2);
            entity.setUserName("Admin User");
            entity.setEmail("admin@example.com");
            entity.setPassword("hashedPassword");
            entity.setRole(Role.ADMIN);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("Debería mapear UserEntity con rol EMPLOYEE")
        void shouldMapUserEntityWithEmployeeRole() {
            // Given
            UserEntity entity = new UserEntity();
            entity.setIdUser(3);
            entity.setUserName("Employee User");
            entity.setEmail("employee@example.com");
            entity.setPassword("hashedPassword");
            entity.setRole(Role.EMPLOYEE);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getRole()).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("Debería mapear UserEntity con campos opcionales nulos")
        void shouldMapUserEntityWithNullOptionalFields() {
            // Given
            UserEntity entity = new UserEntity();
            entity.setIdUser(4);
            entity.setUserName("Minimal User");
            entity.setEmail("minimal@example.com");
            entity.setPassword("hashedPassword");
            entity.setRole(Role.CLIENT);
            entity.setUserImage(null);
            entity.setUserImageKey(null);
            entity.setPhone(null);
            entity.setDeletedAt(null);
            entity.setDeletedBy(null);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getUserImage()).isNull();
            assertThat(user.getPhone()).isNull();
            assertThat(user.getDeletedAt()).isNull();
            assertThat(user.getDeletedBy()).isNull();
        }

        @Test
        @DisplayName("Debería mapear UserEntity eliminado lógicamente")
        void shouldMapSoftDeletedUserEntity() {
            // Given
            LocalDateTime deletedAt = LocalDateTime.now();
            UserEntity entity = new UserEntity();
            entity.setIdUser(5);
            entity.setUserName("Deleted User");
            entity.setEmail("deleted@example.com");
            entity.setPassword("hashedPassword");
            entity.setRole(Role.CLIENT);
            entity.setDeletedAt(deletedAt);
            entity.setDeletedBy(2);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getDeletedAt()).isEqualTo(deletedAt);
            assertThat(user.getDeletedBy()).isEqualTo(2);
        }

        @Test
        @DisplayName("Debería retornar null cuando entity es null")
        void shouldReturnNullWhenEntityIsNull() {
            // When
            User user = mapper.toDomain(null);

            // Then
            assertThat(user).isNull();
        }

        @Test
        @DisplayName("Debería mapear UserEntity con campos de auditoría")
        void shouldMapUserEntityWithAuditFields() {
            // Given
            LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
            LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);

            UserEntity entity = new UserEntity();
            entity.setIdUser(6);
            entity.setUserName("Test User");
            entity.setEmail("test@example.com");
            entity.setPassword("hashedPassword");
            entity.setRole(Role.CLIENT);
            entity.setCreatedAt(createdAt);
            entity.setUpdatedAt(updatedAt);
            entity.setCreatedBy(1);
            entity.setUpdatedBy(2);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user.getCreatedAt()).isEqualTo(createdAt);
            assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(user.getCreatedBy()).isEqualTo(1);
            assertThat(user.getUpdatedBy()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo Domain a Entity")
    class DomainToEntityTests {

        @Test
        @DisplayName("Debería mapear User CLIENT a UserEntity correctamente")
        void shouldMapClientUserToUserEntity() {
            // Given
            User user = User.createClient("Jane Doe", "jane@example.com", "hashedPassword123");

            // When
            UserEntity entity = mapper.toEntity(user);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getUserName()).isEqualTo("Jane Doe");
            assertThat(entity.getEmail()).isEqualTo("jane@example.com");
            assertThat(entity.getPassword()).isEqualTo("hashedPassword123");
            assertThat(entity.getRole()).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería mapear User con rol ADMIN y teléfono")
        void shouldMapUserWithAdminRoleAndPhone() {
            // Given
            User user = User.createByAdmin(
                    "Admin User", "admin@example.com", "hashedPassword",
                    Role.ADMIN, "+573001234567", null, null, 1
            );

            // When
            UserEntity entity = mapper.toEntity(user);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getRole()).isEqualTo(Role.ADMIN);
            assertThat(entity.getPhone()).isEqualTo("+573001234567");
        }

        @Test
        @DisplayName("Debería mapear User con rol EMPLOYEE")
        void shouldMapUserWithEmployeeRole() {
            // Given
            User user = User.createByAdmin(
                    "Employee User", "employee@example.com", "hashedPassword",
                    Role.EMPLOYEE, null, null, null, 1
            );

            // When
            UserEntity entity = mapper.toEntity(user);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getRole()).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("Debería mapear User con imagen y clave")
        void shouldMapUserWithImageAndKey() {
            // Given
            User user = User.createByAdmin(
                    "Image User", "image@example.com", "hashedPassword",
                    Role.CLIENT, null, "image-key-123", "profile.jpg", 1
            );

            // When
            UserEntity entity = mapper.toEntity(user);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getUserImage()).isEqualTo("profile.jpg");
            assertThat(entity.getUserImageKey()).isEqualTo("image-key-123");
        }

        @Test
        @DisplayName("Debería retornar null cuando user es null")
        void shouldReturnNullWhenUserIsNull() {
            // When
            UserEntity entity = mapper.toEntity(null);

            // Then
            assertThat(entity).isNull();
        }

        @Test
        @DisplayName("Debería mapear User creado por admin con todos los campos")
        void shouldMapUserCreatedByAdminWithAllFields() {
            // Given
            User user = User.createByAdmin(
                    "Admin Created User",
                    "admin.created@example.com",
                    "hashedPassword",
                    Role.EMPLOYEE,
                    "+573001234567",
                    "image-key-456",
                    "employee-profile.jpg",
                    1
            );

            // When
            UserEntity entity = mapper.toEntity(user);

            // Then
            assertThat(entity.getUserName()).isEqualTo("Admin Created User");
            assertThat(entity.getEmail()).isEqualTo("admin.created@example.com");
            assertThat(entity.getRole()).isEqualTo(Role.EMPLOYEE);
            assertThat(entity.getPhone()).isEqualTo("+573001234567");
            assertThat(entity.getUserImage()).isEqualTo("employee-profile.jpg");
            assertThat(entity.getUserImageKey()).isEqualTo("image-key-456");
        }
    }

    @Nested
    @DisplayName("Pruebas de Conversión String a Role")
    class StringToRoleTests {

        @Test
        @DisplayName("Debería convertir 'CLIENT' a Role.CLIENT")
        void shouldConvertClientStringToClientRole() {
            // When
            Role role = mapper.stringToRole("CLIENT");

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería convertir 'ADMIN' a Role.ADMIN")
        void shouldConvertAdminStringToAdminRole() {
            // When
            Role role = mapper.stringToRole("ADMIN");

            // Then
            assertThat(role).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("Debería convertir 'EMPLOYEE' a Role.EMPLOYEE")
        void shouldConvertEmployeeStringToEmployeeRole() {
            // When
            Role role = mapper.stringToRole("EMPLOYEE");

            // Then
            assertThat(role).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("Debería convertir string en minúsculas a Role correcto")
        void shouldConvertLowercaseStringToCorrectRole() {
            // When
            Role role = mapper.stringToRole("client");

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería convertir string en mayúsculas y minúsculas mezcladas")
        void shouldConvertMixedCaseStringToCorrectRole() {
            // When
            Role role = mapper.stringToRole("AdMiN");

            // Then
            assertThat(role).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("Debería retornar Role.CLIENT cuando string es null")
        void shouldReturnClientRoleWhenStringIsNull() {
            // When
            Role role = mapper.stringToRole(null);

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería retornar Role.CLIENT cuando string está vacío")
        void shouldReturnClientRoleWhenStringIsEmpty() {
            // When
            Role role = mapper.stringToRole("");

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería retornar Role.CLIENT cuando string contiene solo espacios")
        void shouldReturnClientRoleWhenStringIsBlank() {
            // When
            Role role = mapper.stringToRole("   ");

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería retornar Role.CLIENT cuando string es inválido")
        void shouldReturnClientRoleWhenStringIsInvalid() {
            // When
            Role role = mapper.stringToRole("INVALID_ROLE");

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería retornar Role.CLIENT para string numérico")
        void shouldReturnClientRoleForNumericString() {
            // When
            Role role = mapper.stringToRole("12345");

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Debería retornar Role.CLIENT para string con caracteres especiales")
        void shouldReturnClientRoleForStringWithSpecialCharacters() {
            // When
            Role role = mapper.stringToRole("@#$%");

            // Then
            assertThat(role).isEqualTo(Role.CLIENT);
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
    @DisplayName("Pruebas de Mapeo Bidireccional")
    class BidirectionalMappingTests {

        @Test
        @DisplayName("Debería mantener datos después de mapeo bidireccional Entity->Domain->Entity")
        void shouldMaintainDataAfterBidirectionalMappingEntityToDomainToEntity() {
            // Given
            UserEntity originalEntity = new UserEntity();
            originalEntity.setIdUser(1);
            originalEntity.setUserName("Test User");
            originalEntity.setEmail("test@example.com");
            originalEntity.setPassword("hashedPassword");
            originalEntity.setRole(Role.CLIENT);
            originalEntity.setPhone("+573001234567");

            // When
            User domain = mapper.toDomain(originalEntity);
            UserEntity mappedEntity = mapper.toEntity(domain);

            // Then
            assertThat(mappedEntity.getUserName()).isEqualTo(originalEntity.getUserName());
            assertThat(mappedEntity.getEmail()).isEqualTo(originalEntity.getEmail());
            assertThat(mappedEntity.getPassword()).isEqualTo(originalEntity.getPassword());
            assertThat(mappedEntity.getRole()).isEqualTo(originalEntity.getRole());
            assertThat(mappedEntity.getPhone()).isEqualTo(originalEntity.getPhone());
        }

        @Test
        @DisplayName("Debería mantener datos después de mapeo bidireccional Domain->Entity->Domain")
        void shouldMaintainDataAfterBidirectionalMappingDomainToEntityToDomain() {
            // Given
            User originalUser = User.createByAdmin(
                    "Test User", "test@example.com", "hashedPassword",
                    Role.CLIENT, "+573001234567", null, null, 1
            );

            // When
            UserEntity entity = mapper.toEntity(originalUser);
            User mappedUser = mapper.toDomain(entity);

            // Then
            assertThat(mappedUser.getUserName()).isEqualTo(originalUser.getUserName());
            assertThat(mappedUser.getEmail()).isEqualTo(originalUser.getEmail());
            assertThat(mappedUser.getPassword()).isEqualTo(originalUser.getPassword());
            assertThat(mappedUser.getRole()).isEqualTo(originalUser.getRole());
            assertThat(mappedUser.getPhone()).isEqualTo(originalUser.getPhone());
        }
    }

    @Nested
    @DisplayName("Pruebas de Casos Especiales")
    class SpecialCasesTests {

        @Test
        @DisplayName("Debería mapear UserEntity con email largo")
        void shouldMapUserEntityWithLongEmail() {
            // Given
            String longEmail = "very.long.email.address.test@example.com";
            UserEntity entity = new UserEntity();
            entity.setIdUser(1);
            entity.setUserName("Test User");
            entity.setEmail(longEmail);
            entity.setPassword("hashedPassword");
            entity.setRole(Role.CLIENT);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user.getEmail()).isEqualTo(longEmail);
        }

        @Test
        @DisplayName("Debería mapear UserEntity con password BCrypt")
        void shouldMapUserEntityWithBCryptPassword() {
            // Given
            String bcryptPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
            UserEntity entity = new UserEntity();
            entity.setIdUser(1);
            entity.setUserName("Test User");
            entity.setEmail("test@example.com");
            entity.setPassword(bcryptPassword);
            entity.setRole(Role.CLIENT);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user.getPassword()).isEqualTo(bcryptPassword);
        }

        @Test
        @DisplayName("Debería mapear User con nombre largo")
        void shouldMapUserWithLongName() {
            // Given
            String longName = "Very Long User Name For Testing Purposes";
            User user = User.createClient(longName, "test@example.com", "hashedPassword");

            // When
            UserEntity entity = mapper.toEntity(user);

            // Then
            assertThat(entity.getUserName()).isEqualTo(longName);
        }

        @Test
        @DisplayName("Debería mapear UserEntity con deletedAt y deletedBy")
        void shouldMapUserEntityWithSoftDeleteFields() {
            // Given
            LocalDateTime deletedAt = LocalDateTime.of(2024, 1, 3, 20, 0);

            UserEntity entity = new UserEntity();
            entity.setIdUser(1);
            entity.setUserName("Deleted User");
            entity.setEmail("deleted@example.com");
            entity.setPassword("hashedPassword");
            entity.setRole(Role.CLIENT);
            entity.setDeletedAt(deletedAt);
            entity.setDeletedBy(3);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user.getDeletedAt()).isEqualTo(deletedAt);
            assertThat(user.getDeletedBy()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Pruebas de Todos los Roles")
    class AllRolesTests {

        @Test
        @DisplayName("Debería mapear correctamente todos los roles de Entity a Domain")
        void shouldMapAllRolesFromEntityToDomain() {
            for (Role role : Role.values()) {
                // Given
                UserEntity entity = new UserEntity();
                entity.setIdUser(1);
                entity.setUserName("Test User");
                entity.setEmail("test@example.com");
                entity.setPassword("hashedPassword");
                entity.setRole(role);

                // When
                User user = mapper.toDomain(entity);

                // Then
                assertThat(user.getRole()).isEqualTo(role);
            }
        }

        @Test
        @DisplayName("Debería mapear correctamente todos los roles de Domain a Entity")
        void shouldMapAllRolesFromDomainToEntity() {
            for (Role role : Role.values()) {
                // Given
                User user = User.createByAdmin(
                        "Test User", "test@example.com", "hashedPassword",
                        role, null, null, null, 1
                );

                // When
                UserEntity entity = mapper.toEntity(user);

                // Then
                assertThat(entity.getRole()).isEqualTo(role);
            }
        }

        @Test
        @DisplayName("Debería convertir todos los nombres de roles a enum correctamente")
        void shouldConvertAllRoleNamesToEnumCorrectly() {
            for (Role role : Role.values()) {
                // When
                Role convertedRole = mapper.stringToRole(role.name());

                // Then
                assertThat(convertedRole).isEqualTo(role);
            }
        }

        @Test
        @DisplayName("Debería convertir todos los enum de roles a string correctamente")
        void shouldConvertAllRoleEnumsToStringCorrectly() {
            for (Role role : Role.values()) {
                // When
                String roleString = mapper.roleToString(role);

                // Then
                assertThat(roleString).isEqualTo(role.name());
            }
        }
    }
}
