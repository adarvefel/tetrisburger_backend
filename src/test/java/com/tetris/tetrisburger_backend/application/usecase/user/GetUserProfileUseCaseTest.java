package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.enums.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.GetUserProfileQuery;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import com.tetris.tetrisburger_backend.infrastructure.security.JwtUtil;
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
@DisplayName("Pruebas de GetUserProfileUseCase")
class GetUserProfileUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private GetUserProfileUseCase useCase;

    private User clientUser;
    private User adminUser;
    private User employeeUser;
    private GetUserProfileQuery query;

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

        query = new GetUserProfileQuery(100);
    }

    @Nested
    @DisplayName("Obtención exitosa del perfil")
    class SuccessfulProfileRetrieval {

        @Test
        @DisplayName("debería obtener perfil de usuario CLIENT autenticado usando JWT")
        void shouldGetProfileForAuthenticatedClientUserUsingJwt() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(query);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100);
            assertThat(result.getUserName()).isEqualTo("John Doe");
            assertThat(result.getEmail()).isEqualTo("john@example.com");
            assertThat(result.getRole()).isEqualTo(Role.CLIENT);

            verify(jwtUtil).getUserIdFromContext();
            verify(userRepository).findUserById(100);
        }

        @Test
        @DisplayName("debería obtener perfil de usuario ADMIN autenticado")
        void shouldGetProfileForAuthenticatedAdminUser() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(200);
            when(userRepository.findUserById(200)).thenReturn(Optional.of(adminUser));

            // When
            User result = useCase.execute(new GetUserProfileQuery(200));

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(200);
            assertThat(result.getRole()).isEqualTo(Role.ADMIN);
            assertThat(result.getUserImageKey()).isEqualTo("admin-key");
            assertThat(result.getUserImage()).isEqualTo("admin-image.jpg");
        }

        @Test
        @DisplayName("debería obtener perfil de usuario EMPLOYEE autenticado")
        void shouldGetProfileForAuthenticatedEmployeeUser() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(300);
            when(userRepository.findUserById(300)).thenReturn(Optional.of(employeeUser));

            // When
            User result = useCase.execute(new GetUserProfileQuery(300));

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(300);
            assertThat(result.getRole()).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("debería retornar usuario con todos sus campos")
        void shouldReturnUserWithAllFields() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(query);

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
            when(jwtUtil.getUserIdFromContext()).thenReturn(999);
            when(userRepository.findUserById(999)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.execute(new GetUserProfileQuery(999)))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado");

            verify(jwtUtil).getUserIdFromContext();
            verify(userRepository).findUserById(999);
        }

        @Test
        @DisplayName("debería lanzar excepción sin incluir ID en el mensaje")
        void shouldThrowExceptionWithoutIdInMessage() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.execute(query))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado");
        }
    }

    @Nested
    @DisplayName("Extracción de ID desde JWT (ignora query.idUser)")
    class JwtIdExtraction {

        @Test
        @DisplayName("debería obtener ID del usuario desde el contexto JWT, no del query")
        void shouldGetUserIdFromJwtContextNotFromQuery() {
            // Given - JWT dice ID 100, pero query dice 999
            GetUserProfileQuery queryWithDifferentId = new GetUserProfileQuery(999);
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(queryWithDifferentId);

            // Then
            assertThat(result.getIdUser()).isEqualTo(100); // Usa JWT, no query
            verify(jwtUtil).getUserIdFromContext();
            verify(userRepository).findUserById(100); // Busca por JWT ID
            verify(userRepository, never()).findUserById(999); // NO busca por query ID
        }

        @Test
        @DisplayName("debería ignorar completamente el idUser del query")
        void shouldCompletelyIgnoreIdUserFromQuery() {
            // Given - Query con ID diferente al JWT
            GetUserProfileQuery queryWithWrongId = new GetUserProfileQuery(555);
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(queryWithWrongId);

            // Then
            assertThat(result.getIdUser()).isEqualTo(100); // JWT gana
            verify(userRepository).findUserById(100);
        }

        @Test
        @DisplayName("debería usar el ID extraído del JWT para buscar usuario")
        void shouldUseExtractedIdToFindUser() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(777);
            clientUser.setIdUser(777);
            when(userRepository.findUserById(777)).thenReturn(Optional.of(clientUser));

            // When
            useCase.execute(new GetUserProfileQuery(100)); // Query dice 100, pero JWT dice 777

            // Then
            verify(jwtUtil).getUserIdFromContext();
            verify(userRepository).findUserById(777); // Usa 777 del JWT
        }

        @Test
        @DisplayName("debería propagar excepción si falla extracción de ID desde JWT")
        void shouldPropagateExceptionIfJwtExtractionFails() {
            // Given
            when(jwtUtil.getUserIdFromContext())
                    .thenThrow(new RuntimeException("Token inválido"));

            // When / Then
            assertThatThrownBy(() -> useCase.execute(query))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Token inválido");

            verify(jwtUtil).getUserIdFromContext();
            verify(userRepository, never()).findUserById(anyInt());
        }

        @Test
        @DisplayName("debería manejar SecurityException desde JWT")
        void shouldHandleSecurityExceptionFromJwt() {
            // Given
            when(jwtUtil.getUserIdFromContext())
                    .thenThrow(new SecurityException("No autenticado"));

            // When / Then
            assertThatThrownBy(() -> useCase.execute(query))
                    .isInstanceOf(SecurityException.class)
                    .hasMessage("No autenticado");

            verify(userRepository, never()).findUserById(anyInt());
        }
    }

    @Nested
    @DisplayName("Orden de ejecución")
    class ExecutionOrder {

        @Test
        @DisplayName("debería seguir orden: obtener ID desde JWT -> buscar usuario")
        void shouldFollowCorrectExecutionOrder() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            useCase.execute(query);

            // Then
            var inOrder = inOrder(jwtUtil, userRepository);
            inOrder.verify(jwtUtil).getUserIdFromContext();
            inOrder.verify(userRepository).findUserById(100);
        }

        @Test
        @DisplayName("no debería buscar usuario si falla obtención de ID")
        void shouldNotFindUserIfIdExtractionFails() {
            // Given
            when(jwtUtil.getUserIdFromContext())
                    .thenThrow(new RuntimeException("JWT error"));

            // When / Then
            assertThatThrownBy(() -> useCase.execute(query))
                    .isInstanceOf(RuntimeException.class);

            verify(userRepository, never()).findUserById(anyInt());
        }
    }

    @Nested
    @DisplayName("Verificación de llamadas")
    class CallVerification {

        @Test
        @DisplayName("debería llamar a getUserIdFromContext exactamente una vez")
        void shouldCallGetUserIdFromContextExactlyOnce() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            useCase.execute(query);

            // Then
            verify(jwtUtil, times(1)).getUserIdFromContext();
        }

        @Test
        @DisplayName("debería llamar a findUserById exactamente una vez")
        void shouldCallFindUserByIdExactlyOnce() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            useCase.execute(query);

            // Then
            verify(userRepository, times(1)).findUserById(100);
        }

        @Test
        @DisplayName("no debería llamar a otros métodos del repositorio")
        void shouldNotCallOtherRepositoryMethods() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            useCase.execute(query);

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
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(query);

            // Then
            assertThat(result.isActive()).isTrue();
        }

        @Test
        @DisplayName("debería retornar usuario eliminado (soft delete)")
        void shouldReturnDeletedUser() {
            // Given
            clientUser.markAsDeleted(1);
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(query);

            // Then
            assertThat(result.isActive()).isFalse();
            assertThat(result.getDeletedAt()).isNotNull();
            assertThat(result.getDeletedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería retornar usuario con imagen de perfil")
        void shouldReturnUserWithProfileImage() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(200);
            when(userRepository.findUserById(200)).thenReturn(Optional.of(adminUser));

            // When
            User result = useCase.execute(new GetUserProfileQuery(200));

            // Then
            assertThat(result.getUserImageKey()).isEqualTo("admin-key");
            assertThat(result.getUserImage()).isEqualTo("admin-image.jpg");
        }

        @Test
        @DisplayName("debería retornar usuario sin imagen de perfil")
        void shouldReturnUserWithoutProfileImage() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(query);

            // Then
            assertThat(result.getUserImageKey()).isNull();
            assertThat(result.getUserImage()).isNull();
        }
    }

    @Nested
    @DisplayName("Query parameter (no usado por el caso de uso)")
    class QueryParameter {

        @Test
        @DisplayName("debería aceptar query con null en idUser")
        void shouldAcceptQueryWithNullIdUser() {
            // Given
            GetUserProfileQuery queryWithNull = new GetUserProfileQuery(null);
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(queryWithNull);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100); // Usa JWT
        }

        @Test
        @DisplayName("debería funcionar con cualquier valor en query.idUser")
        void shouldWorkWithAnyValueInQueryIdUser() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result1 = useCase.execute(new GetUserProfileQuery(1));
            User result2 = useCase.execute(new GetUserProfileQuery(999));
            User result3 = useCase.execute(new GetUserProfileQuery(-1));

            // Then
            assertThat(result1.getIdUser()).isEqualTo(100);
            assertThat(result2.getIdUser()).isEqualTo(100);
            assertThat(result3.getIdUser()).isEqualTo(100);
            verify(jwtUtil, times(3)).getUserIdFromContext();
        }
    }

    @Nested
    @DisplayName("Inmutabilidad del resultado")
    class ResultImmutability {

        @Test
        @DisplayName("debería retornar la misma instancia del repositorio")
        void shouldReturnSameInstanceFromRepository() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result = useCase.execute(query);

            // Then
            assertThat(result).isSameAs(clientUser);
        }

        @Test
        @DisplayName("no debería modificar el usuario retornado")
        void shouldNotModifyReturnedUser() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            String originalName = clientUser.getUserName();
            String originalEmail = clientUser.getEmail();

            // When
            User result = useCase.execute(query);

            // Then
            assertThat(result.getUserName()).isEqualTo(originalName);
            assertThat(result.getEmail()).isEqualTo(originalEmail);
        }
    }

    @Nested
    @DisplayName("Manejo de excepciones")
    class ExceptionHandling {

        @Test
        @DisplayName("debería propagar UserNotFoundException")
        void shouldPropagateUserNotFoundException() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.execute(query))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("debería propagar RuntimeException desde JWT")
        void shouldPropagateRuntimeExceptionFromJwt() {
            // Given
            when(jwtUtil.getUserIdFromContext())
                    .thenThrow(new RuntimeException("Error JWT"));

            // When / Then
            assertThatThrownBy(() -> useCase.execute(query))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Error JWT");
        }

        @Test
        @DisplayName("debería propagar RuntimeException desde repositorio")
        void shouldPropagateRuntimeExceptionFromRepository() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100))
                    .thenThrow(new RuntimeException("Error BD"));

            // When / Then
            assertThatThrownBy(() -> useCase.execute(query))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Error BD");
        }
    }

    @Nested
    @DisplayName("Múltiples ejecuciones")
    class MultipleExecutions {

        @Test
        @DisplayName("debería obtener perfil múltiples veces para el mismo usuario")
        void shouldGetProfileMultipleTimesForSameUser() {
            // Given
            when(jwtUtil.getUserIdFromContext()).thenReturn(100);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(clientUser));

            // When
            User result1 = useCase.execute(query);
            User result2 = useCase.execute(new GetUserProfileQuery(100));

            // Then
            assertThat(result1.getIdUser()).isEqualTo(100);
            assertThat(result2.getIdUser()).isEqualTo(100);

            verify(jwtUtil, times(2)).getUserIdFromContext();
            verify(userRepository, times(2)).findUserById(100);
        }
    }
}
