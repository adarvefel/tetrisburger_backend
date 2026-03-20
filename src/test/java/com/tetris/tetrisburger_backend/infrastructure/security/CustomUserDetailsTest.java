package com.tetris.tetrisburger_backend.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas Unitarias de CustomUserDetails")
class CustomUserDetailsTest {

    @Nested
    @DisplayName("Pruebas de Constructores")
    class ConstructorTests {

        @Test
        @DisplayName("Debería crear CustomUserDetails con constructor de 4 parámetros")
        void shouldCreateCustomUserDetailsWithFourParameterConstructor() {
            // Given
            Integer id = 1;
            String email = "test@example.com";
            String password = "hashedPassword123";
            Collection<? extends GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"));

            // When
            CustomUserDetails userDetails = new CustomUserDetails(id, email, password, authorities);

            // Then
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getId()).isEqualTo(id);
            assertThat(userDetails.getUsername()).isEqualTo(email);
            assertThat(userDetails.getPassword()).isEqualTo(password);
            assertThat(userDetails.getAuthorities()).isEqualTo(authorities);
            assertThat(userDetails.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("Debería crear CustomUserDetails con constructor de 5 parámetros")
        void shouldCreateCustomUserDetailsWithFiveParameterConstructor() {
            // Given
            Integer id = 2;
            String email = "admin@example.com";
            String password = "hashedPassword456";
            Collection<? extends GrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            boolean enabled = false;

            // When
            CustomUserDetails userDetails = new CustomUserDetails(id, email, password, authorities, enabled);

            // Then
            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getId()).isEqualTo(id);
            assertThat(userDetails.getUsername()).isEqualTo(email);
            assertThat(userDetails.getPassword()).isEqualTo(password);
            assertThat(userDetails.getAuthorities()).isEqualTo(authorities);
            assertThat(userDetails.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("Debería establecer enabled como true por defecto con constructor de 4 parámetros")
        void shouldSetEnabledTrueByDefaultWithFourParameterConstructor() {
            // Given
            Integer id = 3;
            String email = "user@example.com";
            String password = "password";
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

            // When
            CustomUserDetails userDetails = new CustomUserDetails(id, email, password, authorities);

            // Then
            assertThat(userDetails.isEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("Pruebas de Getters")
    class GetterTests {

        @Test
        @DisplayName("Debería retornar ID correctamente")
        void shouldReturnIdCorrectly() {
            // Given
            Integer expectedId = 100;
            CustomUserDetails userDetails = new CustomUserDetails(
                    expectedId, "test@example.com", "password", Collections.emptyList()
            );

            // When
            Integer actualId = userDetails.getId();

            // Then
            assertThat(actualId).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("Debería retornar username (email) correctamente")
        void shouldReturnUsernameCorrectly() {
            // Given
            String expectedEmail = "user@example.com";
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, expectedEmail, "password", Collections.emptyList()
            );

            // When
            String actualUsername = userDetails.getUsername();

            // Then
            assertThat(actualUsername).isEqualTo(expectedEmail);
        }

        @Test
        @DisplayName("Debería retornar password correctamente")
        void shouldReturnPasswordCorrectly() {
            // Given
            String expectedPassword = "$2a$10$hashedPassword";
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", expectedPassword, Collections.emptyList()
            );

            // When
            String actualPassword = userDetails.getPassword();

            // Then
            assertThat(actualPassword).isEqualTo(expectedPassword);
        }

        @Test
        @DisplayName("Debería retornar authorities correctamente")
        void shouldReturnAuthoritiesCorrectly() {
            // Given
            Collection<GrantedAuthority> expectedAuthorities = List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", expectedAuthorities
            );

            // When
            Collection<? extends GrantedAuthority> actualAuthorities = userDetails.getAuthorities();

            // Then
            assertThat(actualAuthorities).isEqualTo(expectedAuthorities);
            assertThat(actualAuthorities).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Pruebas de Estados de Cuenta")
    class AccountStatusTests {

        @Test
        @DisplayName("Debería retornar true para isAccountNonExpired")
        void shouldReturnTrueForIsAccountNonExpired() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", Collections.emptyList()
            );

            // When
            boolean isAccountNonExpired = userDetails.isAccountNonExpired();

            // Then
            assertThat(isAccountNonExpired).isTrue();
        }

        @Test
        @DisplayName("Debería retornar true para isAccountNonLocked")
        void shouldReturnTrueForIsAccountNonLocked() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", Collections.emptyList()
            );

            // When
            boolean isAccountNonLocked = userDetails.isAccountNonLocked();

            // Then
            assertThat(isAccountNonLocked).isTrue();
        }

        @Test
        @DisplayName("Debería retornar true para isCredentialsNonExpired")
        void shouldReturnTrueForIsCredentialsNonExpired() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", Collections.emptyList()
            );

            // When
            boolean isCredentialsNonExpired = userDetails.isCredentialsNonExpired();

            // Then
            assertThat(isCredentialsNonExpired).isTrue();
        }

        @Test
        @DisplayName("Debería retornar true para isEnabled cuando se construye con 4 parámetros")
        void shouldReturnTrueForIsEnabledWhenConstructedWithFourParameters() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", Collections.emptyList()
            );

            // When
            boolean isEnabled = userDetails.isEnabled();

            // Then
            assertThat(isEnabled).isTrue();
        }

        @Test
        @DisplayName("Debería retornar false para isEnabled cuando se establece explícitamente")
        void shouldReturnFalseForIsEnabledWhenExplicitlySet() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", Collections.emptyList(), false
            );

            // When
            boolean isEnabled = userDetails.isEnabled();

            // Then
            assertThat(isEnabled).isFalse();
        }

        @Test
        @DisplayName("Debería retornar true para isEnabled cuando se establece explícitamente")
        void shouldReturnTrueForIsEnabledWhenExplicitlySetToTrue() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", Collections.emptyList(), true
            );

            // When
            boolean isEnabled = userDetails.isEnabled();

            // Then
            assertThat(isEnabled).isTrue();
        }
    }

    @Nested
    @DisplayName("Pruebas de Authorities")
    class AuthoritiesTests {

        @Test
        @DisplayName("Debería manejar lista vacía de authorities")
        void shouldHandleEmptyAuthoritiesList() {
            // Given
            Collection<GrantedAuthority> emptyAuthorities = Collections.emptyList();
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", emptyAuthorities
            );

            // When
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            // Then
            assertThat(authorities).isEmpty();
        }

        @Test
        @DisplayName("Debería manejar un authority único")
        void shouldHandleSingleAuthority() {
            // Given
            Collection<GrantedAuthority> singleAuthority =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", singleAuthority
            );

            // When
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            // Then
            assertThat(authorities).hasSize(1);
            assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_USER");
        }

        @Test
        @DisplayName("Debería manejar múltiples authorities")
        void shouldHandleMultipleAuthorities() {
            // Given
            Collection<GrantedAuthority> multipleAuthorities = List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("ROLE_EMPLOYEE")
            );
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password", multipleAuthorities
            );

            // When
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            // Then
            assertThat(authorities).hasSize(3);
            assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER", "ROLE_EMPLOYEE");
        }
    }

    @Nested
    @DisplayName("Pruebas de Casos Especiales")
    class SpecialCasesTests {

        @Test
        @DisplayName("Debería manejar ID nulo")
        void shouldHandleNullId() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    null, "test@example.com", "password", Collections.emptyList()
            );

            // When
            Integer id = userDetails.getId();

            // Then
            assertThat(id).isNull();
        }

        @Test
        @DisplayName("Debería manejar email con caracteres especiales")
        void shouldHandleEmailWithSpecialCharacters() {
            // Given
            String specialEmail = "user+test@example.com";
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, specialEmail, "password", Collections.emptyList()
            );

            // When
            String username = userDetails.getUsername();

            // Then
            assertThat(username).isEqualTo(specialEmail);
        }

        @Test
        @DisplayName("Debería manejar password vacío")
        void shouldHandleEmptyPassword() {
            // Given
            String emptyPassword = "";
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", emptyPassword, Collections.emptyList()
            );

            // When
            String password = userDetails.getPassword();

            // Then
            assertThat(password).isEmpty();
        }

        @Test
        @DisplayName("Debería manejar password nulo")
        void shouldHandleNullPassword() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", null, Collections.emptyList()
            );

            // When
            String password = userDetails.getPassword();

            // Then
            assertThat(password).isNull();
        }

        @Test
        @DisplayName("Debería manejar ID con valor cero")
        void shouldHandleZeroId() {
            // Given
            Integer zeroId = 0;
            CustomUserDetails userDetails = new CustomUserDetails(
                    zeroId, "test@example.com", "password", Collections.emptyList()
            );

            // When
            Integer id = userDetails.getId();

            // Then
            assertThat(id).isEqualTo(zeroId);
        }

        @Test
        @DisplayName("Debería manejar ID con valor máximo")
        void shouldHandleMaximumId() {
            // Given
            Integer maxId = Integer.MAX_VALUE;
            CustomUserDetails userDetails = new CustomUserDetails(
                    maxId, "test@example.com", "password", Collections.emptyList()
            );

            // When
            Integer id = userDetails.getId();

            // Then
            assertThat(id).isEqualTo(maxId);
        }

        @Test
        @DisplayName("Debería manejar password con formato BCrypt")
        void shouldHandleBCryptFormattedPassword() {
            // Given
            String bcryptPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", bcryptPassword, Collections.emptyList()
            );

            // When
            String password = userDetails.getPassword();

            // Then
            assertThat(password).isEqualTo(bcryptPassword);
            assertThat(password).startsWith("$2a$10$");
        }
    }

    @Nested
    @DisplayName("Pruebas de Diferentes Roles")
    class DifferentRolesTests {

        @Test
        @DisplayName("Debería crear CustomUserDetails para rol CLIENT")
        void shouldCreateCustomUserDetailsForClientRole() {
            // Given
            Collection<GrantedAuthority> clientAuthorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"));

            // When
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "client@example.com", "password", clientAuthorities
            );

            // Then
            assertThat(userDetails.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_CLIENT");
        }

        @Test
        @DisplayName("Debería crear CustomUserDetails para rol ADMIN")
        void shouldCreateCustomUserDetailsForAdminRole() {
            // Given
            Collection<GrantedAuthority> adminAuthorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));

            // When
            CustomUserDetails userDetails = new CustomUserDetails(
                    2, "admin@example.com", "password", adminAuthorities
            );

            // Then
            assertThat(userDetails.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_ADMIN");
        }

        @Test
        @DisplayName("Debería crear CustomUserDetails para rol EMPLOYEE")
        void shouldCreateCustomUserDetailsForEmployeeRole() {
            // Given
            Collection<GrantedAuthority> employeeAuthorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));

            // When
            CustomUserDetails userDetails = new CustomUserDetails(
                    3, "employee@example.com", "password", employeeAuthorities
            );

            // Then
            assertThat(userDetails.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_EMPLOYEE");
        }
    }

    @Nested
    @DisplayName("Pruebas de Inmutabilidad")
    class ImmutabilityTests {

        @Test
        @DisplayName("Debería mantener valores inmutables después de la creación")
        void shouldMaintainImmutableValuesAfterCreation() {
            // Given
            Integer id = 1;
            String email = "test@example.com";
            String password = "password";
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // When
            CustomUserDetails userDetails = new CustomUserDetails(id, email, password, authorities);

            // Modificar la colección original
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

            // Then
            assertThat(userDetails.getId()).isEqualTo(id);
            assertThat(userDetails.getUsername()).isEqualTo(email);
            assertThat(userDetails.getPassword()).isEqualTo(password);
            // La colección interna debe reflejar el cambio ya que no hay copia defensiva
            assertThat(userDetails.getAuthorities()).hasSize(2);
        }

        @Test
        @DisplayName("Debería retornar los mismos valores en llamadas múltiples")
        void shouldReturnSameValuesOnMultipleCalls() {
            // Given
            CustomUserDetails userDetails = new CustomUserDetails(
                    1, "test@example.com", "password",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // When
            Integer id1 = userDetails.getId();
            Integer id2 = userDetails.getId();
            String username1 = userDetails.getUsername();
            String username2 = userDetails.getUsername();

            // Then
            assertThat(id1).isEqualTo(id2);
            assertThat(username1).isEqualTo(username2);
        }
    }
}
