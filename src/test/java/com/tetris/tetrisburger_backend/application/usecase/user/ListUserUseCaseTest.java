package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
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
@DisplayName("Pruebas de ListUserUseCase")
class ListUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListUserUseCase useCase;

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
                "admin@example.com",
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
    @DisplayName("Listado exitoso con datos")
    class SuccessfulListingWithData {

        @Test
        @DisplayName("debería listar usuarios con paginación por defecto")
        void shouldListUsersWithDefaultPagination() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "idUser");
            List<User> users = Arrays.asList(user1, user2, user3);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,
                    0,
                    10,
                    3,
                    1
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(3);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(3);
            assertThat(result.totalPages()).isEqualTo(1);

            verify(userRepository).findAllUsers(query);
        }

        @Test
        @DisplayName("debería listar usuarios en la primera página")
        void shouldListUsersInFirstPage() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 2, "idUser");
            List<User> users = Arrays.asList(user1, user2);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,
                    0,
                    2,
                    3,
                    2
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.content()).hasSize(2);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.totalElements()).isEqualTo(3);
            assertThat(result.totalPages()).isEqualTo(2);
            assertThat(result.content()).containsExactly(user1, user2);
        }

        @Test
        @DisplayName("debería listar usuarios en la segunda página")
        void shouldListUsersInSecondPage() {
            // Given
            ListUsersQuery query = new ListUsersQuery(1, 2, "idUser");
            List<User> users = Collections.singletonList(user3);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,
                    1,
                    2,
                    3,
                    2
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.content()).hasSize(1);
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.content()).containsExactly(user3);
        }

        @Test
        @DisplayName("debería listar usuarios ordenados por userName")
        void shouldListUsersSortedByUserName() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "userName");
            List<User> users = Arrays.asList(user3, user2, user1);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,
                    0,
                    10,
                    3,
                    1
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.content()).hasSize(3);
            verify(userRepository).findAllUsers(query);
        }

        @Test
        @DisplayName("debería listar usuarios ordenados por email")
        void shouldListUsersSortedByEmail() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "email");
            List<User> users = Arrays.asList(user1, user2, user3);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,
                    0,
                    10,
                    3,
                    1
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.content()).hasSize(3);
            verify(userRepository).findAllUsers(query);
        }
    }

    @Nested
    @DisplayName("Listado con lista vacía")
    class EmptyListing {

        @Test
        @DisplayName("debería retornar página vacía cuando no hay usuarios")
        void shouldReturnEmptyPageWhenNoUsers() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "idUser");
            PageResponse<User> emptyPage = new PageResponse<>(
                    Collections.emptyList(),
                    0,
                    10,
                    0,
                    0
            );

            when(userRepository.findAllUsers(query)).thenReturn(emptyPage);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
            assertThat(result.totalPages()).isEqualTo(0);

            verify(userRepository).findAllUsers(query);
        }

        @Test
        @DisplayName("debería retornar página vacía cuando se solicita página fuera de rango")
        void shouldReturnEmptyPageWhenPageOutOfRange() {
            // Given
            ListUsersQuery query = new ListUsersQuery(10, 10, "idUser");
            PageResponse<User> emptyPage = new PageResponse<>(
                    Collections.emptyList(),
                    10,
                    10,
                    3,
                    1
            );

            when(userRepository.findAllUsers(query)).thenReturn(emptyPage);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.content()).isEmpty();
            assertThat(result.page()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("Diferentes tamaños de página")
    class DifferentPageSizes {

        @Test
        @DisplayName("debería listar con tamaño de página 5")
        void shouldListWithPageSize5() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 5, "idUser");
            List<User> users = Arrays.asList(user1, user2, user3);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,
                    0,
                    5,
                    3,
                    1
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.size()).isEqualTo(5);
            assertThat(result.content()).hasSize(3);
        }

        @Test
        @DisplayName("debería listar con tamaño de página 1")
        void shouldListWithPageSize1() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 1, "idUser");
            List<User> users = Collections.singletonList(user1);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,
                    0,
                    1,
                    3,
                    3
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalPages()).isEqualTo(3);
        }

        @Test
        @DisplayName("debería listar con tamaño de página 100")
        void shouldListWithPageSize100() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 100, "idUser");
            List<User> users = Arrays.asList(user1, user2, user3);
            PageResponse<User> pageResponse = new PageResponse<>(
                    users,
                    0,
                    100,
                    3,
                    1
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.size()).isEqualTo(100);
            assertThat(result.content()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Diferentes criterios de ordenamiento")
    class DifferentSortCriteria {

        @Test
        @DisplayName("debería ordenar por idUser")
        void shouldSortByIdUser() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "idUser");
            List<User> users = Arrays.asList(user1, user2, user3);
            PageResponse<User> pageResponse = new PageResponse<>(users, 0, 10, 3, 1);

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            verify(userRepository).findAllUsers(argThat(q ->
                    "idUser".equals(q.sortBy())
            ));
        }

        @Test
        @DisplayName("debería ordenar por userName")
        void shouldSortByUserName() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "userName");
            List<User> users = Arrays.asList(user1, user2, user3);
            PageResponse<User> pageResponse = new PageResponse<>(users, 0, 10, 3, 1);

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            verify(userRepository).findAllUsers(argThat(q ->
                    "userName".equals(q.sortBy())
            ));
        }

        @Test
        @DisplayName("debería ordenar por email")
        void shouldSortByEmail() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "email");
            List<User> users = Arrays.asList(user1, user2, user3);
            PageResponse<User> pageResponse = new PageResponse<>(users, 0, 10, 3, 1);

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            verify(userRepository).findAllUsers(argThat(q ->
                    "email".equals(q.sortBy())
            ));
        }

        @Test
        @DisplayName("debería ordenar por createdAt")
        void shouldSortByCreatedAt() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "createdAt");
            List<User> users = Arrays.asList(user3, user2, user1);
            PageResponse<User> pageResponse = new PageResponse<>(users, 0, 10, 3, 1);

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            verify(userRepository).findAllUsers(argThat(q ->
                    "createdAt".equals(q.sortBy())
            ));
        }
    }

    @Nested
    @DisplayName("Navegación entre páginas")
    class PageNavigation {

        @Test
        @DisplayName("debería navegar de página 0 a página 1")
        void shouldNavigateFromPage0ToPage1() {
            // Given - Primera página
            ListUsersQuery queryPage0 = new ListUsersQuery(0, 2, "idUser");
            PageResponse<User> page0 = new PageResponse<>(
                    Arrays.asList(user1, user2),
                    0,
                    2,
                    3,
                    2
            );

            // Segunda página
            ListUsersQuery queryPage1 = new ListUsersQuery(1, 2, "idUser");
            PageResponse<User> page1 = new PageResponse<>(
                    Collections.singletonList(user3),
                    1,
                    2,
                    3,
                    2
            );

            when(userRepository.findAllUsers(queryPage0)).thenReturn(page0);
            when(userRepository.findAllUsers(queryPage1)).thenReturn(page1);

            // When
            PageResponse<User> resultPage0 = useCase.execute(queryPage0);
            PageResponse<User> resultPage1 = useCase.execute(queryPage1);

            // Then
            assertThat(resultPage0.page()).isEqualTo(0);
            assertThat(resultPage0.content()).hasSize(2);

            assertThat(resultPage1.page()).isEqualTo(1);
            assertThat(resultPage1.content()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Verificación de llamadas al repositorio")
    class RepositoryCallVerification {

        @Test
        @DisplayName("debería llamar al repositorio exactamente una vez")
        void shouldCallRepositoryExactlyOnce() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "idUser");
            PageResponse<User> pageResponse = new PageResponse<>(
                    Collections.emptyList(),
                    0,
                    10,
                    0,
                    0
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            useCase.execute(query);

            // Then
            verify(userRepository, times(1)).findAllUsers(query);
        }

        @Test
        @DisplayName("debería pasar el query correcto al repositorio")
        void shouldPassCorrectQueryToRepository() {
            // Given
            ListUsersQuery query = new ListUsersQuery(2, 15, "userName");
            PageResponse<User> pageResponse = new PageResponse<>(
                    Collections.emptyList(),
                    2,
                    15,
                    0,
                    0
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            useCase.execute(query);

            // Then
            verify(userRepository).findAllUsers(argThat(q ->
                    q.page() == 2 &&
                            q.size() == 15 &&
                            "userName".equals(q.sortBy())
            ));
        }
    }

    @Nested
    @DisplayName("Metadatos de paginación")
    class PaginationMetadata {

        @Test
        @DisplayName("debería calcular totalPages correctamente")
        void shouldCalculateTotalPagesCorrectly() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 2, "idUser");
            PageResponse<User> pageResponse = new PageResponse<>(
                    Arrays.asList(user1, user2),
                    0,
                    2,
                    5,
                    3
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.totalElements()).isEqualTo(5);
            assertThat(result.totalPages()).isEqualTo(3);
        }

        @Test
        @DisplayName("debería retornar metadatos correctos para página única")
        void shouldReturnCorrectMetadataForSinglePage() {
            // Given
            ListUsersQuery query = new ListUsersQuery(0, 10, "idUser");
            PageResponse<User> pageResponse = new PageResponse<>(
                    Arrays.asList(user1, user2, user3),
                    0,
                    10,
                    3,
                    1
            );

            when(userRepository.findAllUsers(query)).thenReturn(pageResponse);

            // When
            PageResponse<User> result = useCase.execute(query);

            // Then
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(3);
            assertThat(result.totalPages()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Validación del Query")
    class QueryValidation {

        @Test
        @DisplayName("debería lanzar excepción cuando page es negativo")
        void shouldThrowExceptionWhenPageIsNegative() {
            // When / Then
            assertThatThrownBy(() ->
                    new ListUsersQuery(-1, 10, "idUser")
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La página no puede ser negativa");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando size es cero")
        void shouldThrowExceptionWhenSizeIsZero() {
            // When / Then
            assertThatThrownBy(() ->
                    new ListUsersQuery(0, 0, "idUser")
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El tamaño debe estar entre 1 y 100");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando size es negativo")
        void shouldThrowExceptionWhenSizeIsNegative() {
            // When / Then
            assertThatThrownBy(() ->
                    new ListUsersQuery(0, -5, "idUser")
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El tamaño debe estar entre 1 y 100");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando size es mayor que 100")
        void shouldThrowExceptionWhenSizeIsGreaterThan100() {
            // When / Then
            assertThatThrownBy(() ->
                    new ListUsersQuery(0, 101, "idUser")
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El tamaño debe estar entre 1 y 100");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando sortBy es null")
        void shouldThrowExceptionWhenSortByIsNull() {
            // When / Then
            assertThatThrownBy(() ->
                    new ListUsersQuery(0, 10, null)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El campo de ordenamiento es requerido");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando sortBy está vacío")
        void shouldThrowExceptionWhenSortByIsEmpty() {
            // When / Then
            assertThatThrownBy(() ->
                    new ListUsersQuery(0, 10, "")
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El campo de ordenamiento es requerido");
        }

        @Test
        @DisplayName("debería lanzar excepción cuando sortBy solo contiene espacios")
        void shouldThrowExceptionWhenSortByIsBlank() {
            // When / Then
            assertThatThrownBy(() ->
                    new ListUsersQuery(0, 10, "   ")
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El campo de ordenamiento es requerido");
        }

        @Test
        @DisplayName("debería aceptar valores válidos en los límites")
        void shouldAcceptValidBoundaryValues() {
            // When / Then - No debe lanzar excepción
            assertThatNoException().isThrownBy(() -> {
                new ListUsersQuery(0, 1, "idUser");
                new ListUsersQuery(0, 100, "idUser");
            });
        }
    }
}
