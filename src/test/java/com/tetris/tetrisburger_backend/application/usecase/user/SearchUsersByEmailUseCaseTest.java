package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de SearchUsersByEmailUseCase")
class SearchUsersByEmailUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SearchUsersByEmailUseCase useCase;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = User.createClient(
                "John Doe",
                "john@example.com",
                "$2a$10$hash1"
        );
        user1.setIdUser(1);

        user2 = User.createClient(
                "Jane Smith",
                "jane@example.com",
                "$2a$10$hash2"
        );
        user2.setIdUser(2);

        user3 = User.createByAdmin(
                "Admin User",
                "admin@test.com",
                "$2a$10$hash3",
                Role.ADMIN,
                "1234567890",
                null,
                null,
                1
        );
        user3.setIdUser(3);
    }

    @Nested
    @DisplayName("Búsqueda exitosa con resultados")
    class SuccessfulSearchWithResults {

        @Test
        @DisplayName("debería encontrar usuarios por parte del email")
        void shouldFindUsersByEmailPart() {
            // Given
            String emailPart = "example";
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(user1, user2);

            verify(userRepository).searchUsersByEmail(emailPart);
        }

        @Test
        @DisplayName("debería encontrar un solo usuario por email específico")
        void shouldFindSingleUserBySpecificEmail() {
            // Given
            String emailPart = "john@example.com";
            List<User> expectedUsers = Collections.singletonList(user1);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("debería buscar por dominio de email")
        void shouldSearchByEmailDomain() {
            // Given
            String emailPart = "example.com";
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> user.getEmail().contains("example.com"));
        }

        @Test
        @DisplayName("debería buscar por nombre de usuario en email")
        void shouldSearchByUsernameInEmail() {
            // Given
            String emailPart = "john";
            List<User> expectedUsers = Collections.singletonList(user1);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).contains("john");
        }

        @Test
        @DisplayName("debería encontrar usuarios con diferentes roles")
        void shouldFindUsersWithDifferentRoles() {
            // Given
            String emailPart = "com";
            List<User> expectedUsers = Arrays.asList(user1, user2, user3);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).extracting(User::getRole)
                    .contains(Role.CLIENT, Role.ADMIN);
        }
    }

    @Nested
    @DisplayName("Búsqueda sin resultados")
    class SearchWithNoResults {

        @Test
        @DisplayName("debería retornar lista vacía cuando no hay coincidencias")
        void shouldReturnEmptyListWhenNoMatches() {
            // Given
            String emailPart = "nonexistent";
            List<User> emptyList = Collections.emptyList();

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(emptyList);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            verify(userRepository).searchUsersByEmail(emailPart);
        }

        @Test
        @DisplayName("debería retornar lista vacía con búsqueda de email completo no existente")
        void shouldReturnEmptyListForNonExistentFullEmail() {
            // Given
            String emailPart = "notfound@example.com";
            List<User> emptyList = Collections.emptyList();

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(emptyList);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Diferentes formatos de búsqueda")
    class DifferentSearchFormats {

        @Test
        @DisplayName("debería buscar con caracteres en minúsculas")
        void shouldSearchWithLowercaseCharacters() {
            // Given
            String emailPart = "example";
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(2);
            verify(userRepository).searchUsersByEmail("example");
        }

        @Test
        @DisplayName("debería buscar con caracteres en mayúsculas")
        void shouldSearchWithUppercaseCharacters() {
            // Given
            String emailPart = "EXAMPLE";
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(2);
            verify(userRepository).searchUsersByEmail("EXAMPLE");
        }

        @Test
        @DisplayName("debería buscar con arroba (@)")
        void shouldSearchWithAtSymbol() {
            // Given
            String emailPart = "@example";
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(2);
            verify(userRepository).searchUsersByEmail("@example");
        }

        @Test
        @DisplayName("debería buscar con punto (.)")
        void shouldSearchWithDot() {
            // Given
            String emailPart = ".com";
            List<User> expectedUsers = Arrays.asList(user1, user2, user3);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("debería buscar con un solo carácter")
        void shouldSearchWithSingleCharacter() {
            // Given
            String emailPart = "j";
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("debería buscar con múltiples caracteres")
        void shouldSearchWithMultipleCharacters() {
            // Given
            String emailPart = "john@example";
            List<User> expectedUsers = Collections.singletonList(user1);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Verificación de llamadas al repositorio")
    class RepositoryCallVerification {

        @Test
        @DisplayName("debería llamar al repositorio exactamente una vez")
        void shouldCallRepositoryExactlyOnce() {
            // Given
            String emailPart = "test";
            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(Collections.emptyList());

            // When
            useCase.handle(emailPart);

            // Then
            verify(userRepository, times(1)).searchUsersByEmail(emailPart);
        }

        @Test
        @DisplayName("debería pasar el emailPart correcto al repositorio")
        void shouldPassCorrectEmailPartToRepository() {
            // Given
            String emailPart = "specificSearch";
            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(Collections.emptyList());

            // When
            useCase.handle(emailPart);

            // Then
            verify(userRepository).searchUsersByEmail("specificSearch");
        }

        @Test
        @DisplayName("no debería modificar el emailPart antes de pasar al repositorio")
        void shouldNotModifyEmailPartBeforePassingToRepository() {
            // Given
            String emailPart = "  spaces  ";
            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(Collections.emptyList());

            // When
            useCase.handle(emailPart);

            // Then
            verify(userRepository).searchUsersByEmail("  spaces  ");
        }
    }

    @Nested
    @DisplayName("Búsquedas con strings especiales")
    class SearchWithSpecialStrings {

        @Test
        @DisplayName("debería manejar string vacío")
        void shouldHandleEmptyString() {
            // Given
            String emailPart = "";
            List<User> allUsers = Arrays.asList(user1, user2, user3);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(allUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(3);
            verify(userRepository).searchUsersByEmail("");
        }

        @Test
        @DisplayName("debería manejar espacios")
        void shouldHandleSpaces() {
            // Given
            String emailPart = "   ";
            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(Collections.emptyList());

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).searchUsersByEmail("   ");
        }

        @Test
        @DisplayName("debería manejar caracteres especiales")
        void shouldHandleSpecialCharacters() {
            // Given
            String emailPart = "user+tag@";
            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(Collections.emptyList());

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            verify(userRepository).searchUsersByEmail("user+tag@");
        }

        @Test
        @DisplayName("debería manejar null como parámetro")
        void shouldHandleNullParameter() {
            // Given
            String emailPart = null;
            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(Collections.emptyList());

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).searchUsersByEmail(null);
        }
    }

    @Nested
    @DisplayName("Retorno de datos completos")
    class CompleteDataReturn {

        @Test
        @DisplayName("debería retornar usuarios con todos sus campos")
        void shouldReturnUsersWithAllFields() {
            // Given
            String emailPart = "john";
            List<User> expectedUsers = Collections.singletonList(user1);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            User foundUser = result.get(0);
            assertThat(foundUser.getIdUser()).isEqualTo(1);
            assertThat(foundUser.getUserName()).isEqualTo("John Doe");
            assertThat(foundUser.getEmail()).isEqualTo("john@example.com");
            assertThat(foundUser.getRole()).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("debería retornar lista tal cual viene del repositorio")
        void shouldReturnListAsIsFromRepository() {
            // Given
            String emailPart = "example";
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result).isSameAs(expectedUsers);
        }
    }

    @Nested
    @DisplayName("Orden de resultados")
    class ResultsOrder {

        @Test
        @DisplayName("debería mantener el orden retornado por el repositorio")
        void shouldMaintainOrderReturnedByRepository() {
            // Given
            String emailPart = "example";
            List<User> orderedUsers = Arrays.asList(user2, user1); // Jane antes que John

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(orderedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).containsExactly(user2, user1);
        }

        @Test
        @DisplayName("debería respetar orden alfabético si viene del repositorio")
        void shouldRespectAlphabeticalOrderFromRepository() {
            // Given
            String emailPart = "com";
            List<User> alphabeticalUsers = Arrays.asList(user3, user2, user1);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(alphabeticalUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).containsExactly(user3, user2, user1);
        }
    }

    @Nested
    @DisplayName("Casos edge de búsqueda")
    class SearchEdgeCases {

        @Test
        @DisplayName("debería buscar con parte inicial del email")
        void shouldSearchWithEmailPrefix() {
            // Given
            String emailPart = "john";
            List<User> expectedUsers = Collections.singletonList(user1);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getEmail()).startsWith("john");
        }

        @Test
        @DisplayName("debería buscar con parte final del email")
        void shouldSearchWithEmailSuffix() {
            // Given
            String emailPart = ".com";
            List<User> expectedUsers = Arrays.asList(user1, user2, user3);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(user -> user.getEmail().endsWith(".com"));
        }

        @Test
        @DisplayName("debería buscar con parte intermedia del email")
        void shouldSearchWithEmailMiddlePart() {
            // Given
            String emailPart = "@example.";
            List<User> expectedUsers = Arrays.asList(user1, user2);

            when(userRepository.searchUsersByEmail(emailPart)).thenReturn(expectedUsers);

            // When
            List<User> result = useCase.handle(emailPart);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(user -> user.getEmail().contains("@example."));
        }
    }
}
