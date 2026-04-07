package com.tetris.tetrisburger_backend.infrastructure.security;

import com.tetris.tetrisburger_backend.domain.enums.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de UserSecurityDetailsService")
class UserSecurityDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSecurityDetailsService userSecurityDetailsService;

    @Nested
    @DisplayName("Pruebas de Carga de Usuario Exitosa")
    class SuccessfulUserLoadingTests {

        @Test
        @DisplayName("Debería cargar usuario CLIENT correctamente")
        void shouldLoadClientUserSuccessfully() {
            // Given
            String email = "client@example.com";
            User clientUser = User.createClient("Client User", email, "hashedPassword123");
            clientUser.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(clientUser));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo(email);
            assertThat(userDetails.getPassword()).isEqualTo("hashedPassword123");
            assertThat(userDetails.getAuthorities()).hasSize(1);
            assertThat(userDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .contains("ROLE_CLIENT");
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería cargar usuario ADMIN correctamente")
        void shouldLoadAdminUserSuccessfully() {
            // Given
            String email = "admin@example.com";
            User adminUser = User.createByAdmin(
                    "Admin User", email, "hashedPassword123",
                    Role.ADMIN, null, null, null, 1
            );
            adminUser.setIdUser(2);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(adminUser));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo(email);
            assertThat(userDetails.getPassword()).isEqualTo("hashedPassword123");
            assertThat(userDetails.getAuthorities()).hasSize(1);
            assertThat(userDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .contains("ROLE_ADMIN");
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería cargar usuario EMPLOYEE correctamente")
        void shouldLoadEmployeeUserSuccessfully() {
            // Given
            String email = "employee@example.com";
            User employeeUser = User.createByAdmin(
                    "Employee User", email, "hashedPassword123",
                    Role.EMPLOYEE, null, null, null, 1
            );
            employeeUser.setIdUser(3);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(employeeUser));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo(email);
            assertThat(userDetails.getPassword()).isEqualTo("hashedPassword123");
            assertThat(userDetails.getAuthorities()).hasSize(1);
            assertThat(userDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .contains("ROLE_EMPLOYEE");
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería retornar CustomUserDetails con ID de usuario")
        void shouldReturnCustomUserDetailsWithUserId() {
            // Given
            String email = "test@example.com";
            Integer expectedUserId = 10;
            User user = User.createClient("Test User", email, "hashedPassword123");
            user.setIdUser(expectedUserId);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            assertThat(customUserDetails.getId()).isEqualTo(expectedUserId);
            verify(userRepository).findUserByEmail(email);
        }
    }

    @Nested
    @DisplayName("Pruebas de Usuario No Encontrado")
    class UserNotFoundTests {

        @Test
        @DisplayName("Debería lanzar UsernameNotFoundException cuando usuario no existe")
        void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            String email = "nonexistent@example.com";
            when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userSecurityDetailsService.loadUserByUsername(email))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("Usuario no encontrado con el Email: " + email);

            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería lanzar UsernameNotFoundException con email específico")
        void shouldThrowUsernameNotFoundExceptionWithSpecificEmail() {
            // Given
            String email = "unknown@test.com";
            when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userSecurityDetailsService.loadUserByUsername(email))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining(email);

            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería lanzar UsernameNotFoundException para email vacío")
        void shouldThrowUsernameNotFoundExceptionForEmptyEmail() {
            // Given
            String email = "";
            when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userSecurityDetailsService.loadUserByUsername(email))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("Usuario no encontrado con el Email: " + email);

            verify(userRepository).findUserByEmail(email);
        }
    }

    @Nested
    @DisplayName("Pruebas de Authorities")
    class AuthoritiesTests {

        @Test
        @DisplayName("Debería asignar authority con prefijo ROLE_")
        void shouldAssignAuthorityWithRolePrefix() {
            // Given
            String email = "test@example.com";
            User user = User.createByAdmin(
                    "Test User", email, "hashedPassword123",
                    Role.ADMIN, null, null, null, 1
            );
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails.getAuthorities()).hasSize(1);
            assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
                    .startsWith("ROLE_");
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería crear authority correctamente para cada rol")
        void shouldCreateAuthorityCorrectlyForEachRole() {
            // Given
            String email = "test@example.com";

            // Test CLIENT
            User clientUser = User.createClient("Client", email, "pass123");
            clientUser.setIdUser(1);
            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(clientUser));
            UserDetails clientDetails = userSecurityDetailsService.loadUserByUsername(email);
            assertThat(clientDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_CLIENT");

            // Test ADMIN
            User adminUser = User.createByAdmin("Admin", email, "pass123", Role.ADMIN, null, null, null, 1);
            adminUser.setIdUser(2);
            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(adminUser));
            UserDetails adminDetails = userSecurityDetailsService.loadUserByUsername(email);
            assertThat(adminDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_ADMIN");

            // Test EMPLOYEE
            User employeeUser = User.createByAdmin("Employee", email, "pass123", Role.EMPLOYEE, null, null, null, 1);
            employeeUser.setIdUser(3);
            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(employeeUser));
            UserDetails employeeDetails = userSecurityDetailsService.loadUserByUsername(email);
            assertThat(employeeDetails.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_EMPLOYEE");

            verify(userRepository, times(3)).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería retornar lista de authorities no vacía")
        void shouldReturnNonEmptyAuthoritiesList() {
            // Given
            String email = "test@example.com";
            User user = User.createClient("Test User", email, "hashedPassword123");
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails.getAuthorities()).isNotEmpty();
            assertThat(userDetails.getAuthorities()).hasSize(1);
            verify(userRepository).findUserByEmail(email);
        }
    }

    @Nested
    @DisplayName("Pruebas de Mapeo de Datos")
    class DataMappingTests {

        @Test
        @DisplayName("Debería mapear correctamente email como username")
        void shouldMapEmailAsUsernameCorrectly() {
            // Given
            String email = "user@example.com";
            User user = User.createClient("Test User", email, "hashedPassword123");
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails.getUsername()).isEqualTo(email);
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería mapear correctamente password hasheado")
        void shouldMapHashedPasswordCorrectly() {
            // Given
            String email = "user@example.com";
            String hashedPassword = "$2a$10$hashedPasswordExample";
            User user = User.createClient("Test User", email, hashedPassword);
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails.getPassword()).isEqualTo(hashedPassword);
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería preservar ID de usuario en CustomUserDetails")
        void shouldPreserveUserIdInCustomUserDetails() {
            // Given
            String email = "user@example.com";
            Integer userId = 42;
            User user = User.createClient("Test User", email, "password123");
            user.setIdUser(userId);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            assertThat(customUserDetails.getId()).isEqualTo(userId);
            verify(userRepository).findUserByEmail(email);
        }
    }

    @Nested
    @DisplayName("Pruebas de Integración con UserRepository")
    class UserRepositoryIntegrationTests {

        @Test
        @DisplayName("Debería llamar findUserByEmail exactamente una vez")
        void shouldCallFindUserByEmailExactlyOnce() {
            // Given
            String email = "test@example.com";
            User user = User.createClient("Test User", email, "hashedPassword123");
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            userSecurityDetailsService.loadUserByUsername(email);

            // Then
            verify(userRepository, times(1)).findUserByEmail(email);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Debería propagar excepción del repositorio")
        void shouldPropagateRepositoryException() {
            // Given
            String email = "test@example.com";
            when(userRepository.findUserByEmail(email))
                    .thenThrow(new RuntimeException("Database connection error"));

            // When & Then
            assertThatThrownBy(() -> userSecurityDetailsService.loadUserByUsername(email))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection error");

            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería buscar por email exacto proporcionado")
        void shouldSearchByExactEmailProvided() {
            // Given
            String email = "exact.email@example.com";
            User user = User.createClient("Test User", email, "hashedPassword123");
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            userSecurityDetailsService.loadUserByUsername(email);

            // Then
            verify(userRepository).findUserByEmail(eq(email));
        }
    }

    @Nested
    @DisplayName("Pruebas de Casos Especiales")
    class SpecialCasesTests {

        @Test
        @DisplayName("Debería manejar email con caracteres especiales")
        void shouldHandleEmailWithSpecialCharacters() {
            // Given
            String email = "user+test@example.com";
            User user = User.createClient("Test User", email, "hashedPassword123");
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails.getUsername()).isEqualTo(email);
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería manejar email con mayúsculas")
        void shouldHandleEmailWithUpperCase() {
            // Given
            String email = "User@Example.COM";
            User user = User.createClient("Test User", email, "hashedPassword123");
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails.getUsername()).isEqualTo(email);
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería manejar usuario con ID positivo grande")
        void shouldHandleUserWithLargePositiveId() {
            // Given
            String email = "test@example.com";
            Integer largeId = Integer.MAX_VALUE;
            User user = User.createClient("Test User", email, "hashedPassword123");
            user.setIdUser(largeId);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            assertThat(customUserDetails.getId()).isEqualTo(largeId);
            verify(userRepository).findUserByEmail(email);
        }

        @Test
        @DisplayName("Debería manejar password con caracteres especiales")
        void shouldHandlePasswordWithSpecialCharacters() {
            // Given
            String email = "test@example.com";
            String specialPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
            User user = User.createClient("Test User", email, specialPassword);
            user.setIdUser(1);

            when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

            // When
            UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

            // Then
            assertThat(userDetails.getPassword()).isEqualTo(specialPassword);
            verify(userRepository).findUserByEmail(email);
        }
    }

    @Nested
    @DisplayName("Pruebas de Todos los Roles")
    class AllRolesTests {

        @Test
        @DisplayName("Debería cargar correctamente todos los tipos de roles")
        void shouldLoadAllRoleTypesCorrectly() {
            // Given
            String email = "test@example.com";

            // When & Then para cada rol
            for (Role role : Role.values()) {
                User user = User.createByAdmin(
                        "User " + role.name(),
                        email,
                        "hashedPassword123",
                        role,
                        null, null, null, 1
                );
                user.setIdUser(1);

                when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

                UserDetails userDetails = userSecurityDetailsService.loadUserByUsername(email);

                assertThat(userDetails.getAuthorities())
                        .extracting(GrantedAuthority::getAuthority)
                        .containsExactly("ROLE_" + role.name());
            }

            verify(userRepository, times(Role.values().length)).findUserByEmail(email);
        }
    }
}
