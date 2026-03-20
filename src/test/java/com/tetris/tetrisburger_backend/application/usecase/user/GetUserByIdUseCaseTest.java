package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de GetUserByIdUseCase")
class GetUserByIdUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserByIdUseCase useCase;

    private User clientUser;
    private User adminUser;
    private User employeeUser;

    @BeforeEach
    void setUp() {
        clientUser = User.createClient(
                "John Doe",
                "john@example.com",
                "$2a$10$hashedPassword"
        );
        clientUser.setIdUser(100);

        adminUser = User.createByAdmin(
                "Admin User",
                "admin@example.com",
                "$2a$10$hashedPassword",
                Role.ADMIN,
                "1234567890",
                "admin-key",
                "admin-image.jpg",
                1
        );
        adminUser.setIdUser(200);

        employeeUser = User.createByAdmin(
                "Employee User",
                "employee@example.com",
                "$2a$10$hashedPassword",
                Role.EMPLOYEE,
                "9876543210",
                null,
                null,
                1
        );
        employeeUser.setIdUser(300);
    }

    @Nested
    @DisplayName("Búsqueda exitosa")
    class SuccessfulSearch {

        @Test
        @DisplayName("debería retornar usuario cuando existe")
        void shouldReturnUserWhenExists() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100);
            assertThat(result.getUserName()).isEqualTo("John Doe");
            assertThat(result.getEmail()).isEqualTo("john@example.com");

            verify(userRepository).findUserById(100);
        }

        @Test
        @DisplayName("debería retornar usuario CLIENT correctamente")
        void shouldReturnClientUserCorrectly() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result.getRole()).isEqualTo(Role.CLIENT);
            assertThat(result.getIdUser()).isEqualTo(100);
        }

        @Test
        @DisplayName("debería retornar usuario ADMIN correctamente")
        void shouldReturnAdminUserCorrectly() {
            // Given
            when(userRepository.findUserById(200)).thenReturn(Optional.of(adminUser));

            // When
            User result = useCase.handle(200);

            // Then
            assertThat(result.getRole()).isEqualTo(Role.ADMIN);
            assertThat(result.getIdUser()).isEqualTo(200);
            assertThat(result.getUserImageKey()).isEqualTo("admin-key");
            assertThat(result.getUserImage()).isEqualTo("admin-image.jpg");
        }

        @Test
        @DisplayName("debería retornar usuario EMPLOYEE correctamente")
        void shouldReturnEmployeeUserCorrectly() {
            // Given
            when(userRepository.findUserById(300)).thenReturn(Optional.of(employeeUser));

            // When
            User result = useCase.handle(300);

            // Then
            assertThat(result.getRole()).isEqualTo(Role.EMPLOYEE);
            assertThat(result.getIdUser()).isEqualTo(300);
        }

        @Test
        @DisplayName("debería retornar usuario con todos sus campos")
        void shouldReturnUserWithAllFields() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result.getIdUser()).isNotNull();
            assertThat(result.getUserName()).isNotNull();
            assertThat(result.getEmail()).isNotNull();
            assertThat(result.getPassword()).isNotNull();
            assertThat(result.getRole()).isNotNull();
            assertThat(result.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("Usuario no encontrado")
    class UserNotFound {

        @Test
        @DisplayName("debería lanzar UserNotFoundException cuando usuario no existe")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            when(userRepository.findUserById(999)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(999))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado con ID:999");

            verify(userRepository).findUserById(999);
        }

        @Test
        @DisplayName("debería incluir el ID en el mensaje de error")
        void shouldIncludeIdInErrorMessage() {
            // Given
            when(userRepository.findUserById(12345)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(12345))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("12345");
        }

        @Test
        @DisplayName("debería lanzar excepción con diferentes IDs no encontrados")
        void shouldThrowExceptionWithDifferentNotFoundIds() {
            // Given
            when(userRepository.findUserById(anyInt())).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(1))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado con ID:1");

            assertThatThrownBy(() -> useCase.handle(100))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado con ID:100");

            assertThatThrownBy(() -> useCase.handle(5000))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado con ID:5000");
        }
    }

    @Nested
    @DisplayName("Verificación de llamadas al repositorio")
    class RepositoryCallVerification {

        @Test
        @DisplayName("debería llamar al repositorio exactamente una vez")
        void shouldCallRepositoryExactlyOnce() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            useCase.handle(100);

            // Then
            verify(userRepository, times(1)).findUserById(100);
        }

        @Test
        @DisplayName("debería pasar el ID correcto al repositorio")
        void shouldPassCorrectIdToRepository() {
            // Given
            when(userRepository.findUserById(777)).thenReturn(Optional.of(clientUser));

            // When
            useCase.handle(777);

            // Then
            verify(userRepository).findUserById(777);
        }

        @Test
        @DisplayName("no debería llamar a otros métodos del repositorio")
        void shouldNotCallOtherRepositoryMethods() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            useCase.handle(100);

            // Then
            verify(userRepository).findUserById(100);
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("Usuarios con diferentes estados")
    class UsersWithDifferentStates {

        @Test
        @DisplayName("debería retornar usuario activo")
        void shouldReturnActiveUser() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result.isActive()).isTrue();
        }

        @Test
        @DisplayName("debería retornar usuario eliminado (soft delete)")
        void shouldReturnDeletedUser() {
            // Given
            clientUser.markAsDeleted(1);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result.isActive()).isFalse();
            assertThat(result.getDeletedAt()).isNotNull();
            assertThat(result.getDeletedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería retornar usuario con imagen")
        void shouldReturnUserWithImage() {
            // Given
            when(userRepository.findUserById(200)).thenReturn(Optional.of(adminUser));

            // When
            User result = useCase.handle(200);

            // Then
            assertThat(result.getUserImageKey()).isEqualTo("admin-key");
            assertThat(result.getUserImage()).isEqualTo("admin-image.jpg");
        }

        @Test
        @DisplayName("debería retornar usuario sin imagen")
        void shouldReturnUserWithoutImage() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result.getUserImageKey()).isNull();
            assertThat(result.getUserImage()).isNull();
        }
    }

    @Nested
    @DisplayName("Casos edge con IDs")
    class EdgeCasesWithIds {

        @Test
        @DisplayName("debería buscar con ID mínimo positivo")
        void shouldSearchWithMinimumPositiveId() {
            // Given
            clientUser.setIdUser(1);
            when(userRepository.findUserById(1)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(1);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería buscar con ID grande")
        void shouldSearchWithLargeId() {
            // Given
            clientUser.setIdUser(999999);
            when(userRepository.findUserById(999999)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(999999);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(999999);
        }

        @Test
        @DisplayName("debería manejar búsqueda con ID cero")
        void shouldHandleSearchWithZeroId() {
            // Given
            when(userRepository.findUserById(0)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(0))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado con ID:0");
        }

        @Test
        @DisplayName("debería manejar búsqueda con ID negativo")
        void shouldHandleSearchWithNegativeId() {
            // Given
            when(userRepository.findUserById(-1)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(-1))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado con ID:-1");
        }
    }

    @Nested
    @DisplayName("Inmutabilidad del resultado")
    class ResultImmutability {

        @Test
        @DisplayName("debería retornar la misma instancia del repositorio")
        void shouldReturnSameInstanceFromRepository() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result).isSameAs(clientUser);
        }

        @Test
        @DisplayName("no debería modificar el usuario retornado")
        void shouldNotModifyReturnedUser() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            String originalName = clientUser.getUserName();
            String originalEmail = clientUser.getEmail();

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result.getUserName()).isEqualTo(originalName);
            assertThat(result.getEmail()).isEqualTo(originalEmail);
        }
    }

    @Nested
    @DisplayName("Múltiples búsquedas")
    class MultipleSearches {

        @Test
        @DisplayName("debería buscar diferentes usuarios en múltiples llamadas")
        void shouldSearchDifferentUsersInMultipleCalls() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));
            when(userRepository.findUserById(200)).thenReturn(Optional.of(adminUser));

            // When
            User result1 = useCase.handle(100);
            User result2 = useCase.handle(200);

            // Then
            assertThat(result1.getIdUser()).isEqualTo(100);
            assertThat(result2.getIdUser()).isEqualTo(200);

            verify(userRepository).findUserById(100);
            verify(userRepository).findUserById(200);
        }

        @Test
        @DisplayName("debería buscar el mismo usuario múltiples veces")
        void shouldSearchSameUserMultipleTimes() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result1 = useCase.handle(100);
            User result2 = useCase.handle(100);

            // Then
            assertThat(result1).isEqualTo(result2);
            verify(userRepository, times(2)).findUserById(100);
        }
    }

    @Nested
    @DisplayName("Comportamiento con Optional")
    class OptionalBehavior {

        @Test
        @DisplayName("debería extraer usuario de Optional exitosamente")
        void shouldExtractUserFromOptionalSuccessfully() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.handle(100);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(User.class);
        }

        @Test
        @DisplayName("debería lanzar excepción cuando Optional está vacío")
        void shouldThrowExceptionWhenOptionalIsEmpty() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(100))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
