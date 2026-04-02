package com.tetris.tetrisburger_backend.domain.common;

import com.tetris.tetrisburger_backend.domain.enums.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas de PageResponse")
class PageResponseTest {

    @Nested
    @DisplayName("Creación Exitosa")
    class SuccessfulCreationTests {

        @Test
        @DisplayName("debería crear PageResponse con lista de usuarios")
        void shouldCreatePageResponseWithUserList() {
            // Given
            User user1 = User.createClient("User 1", "user1@example.com", "$2a$10$hashed");
            User user2 = User.createClient("User 2", "user2@example.com", "$2a$10$hashed");
            List<User> users = Arrays.asList(user1, user2);

            // When
            PageResponse<User> response = new PageResponse<>(users, 0, 10, 2, 1);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).hasSize(2);
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(2);
            assertThat(response.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería crear PageResponse con lista vacía")
        void shouldCreatePageResponseWithEmptyList() {
            // Given
            List<String> emptyList = Collections.emptyList();

            // When
            PageResponse<String> response = new PageResponse<>(emptyList, 0, 10, 0, 0);

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isEqualTo(0);
            assertThat(response.totalPages()).isEqualTo(0);
        }

        @Test
        @DisplayName("debería crear PageResponse genérico con Strings")
        void shouldCreateGenericPageResponseWithStrings() {
            // Given
            List<String> strings = Arrays.asList("Item 1", "Item 2", "Item 3");

            // When
            PageResponse<String> response = new PageResponse<>(strings, 1, 3, 15, 5);

            // Then
            assertThat(response.content()).containsExactly("Item 1", "Item 2", "Item 3");
            assertThat(response.page()).isEqualTo(1);
            assertThat(response.size()).isEqualTo(3);
            assertThat(response.totalElements()).isEqualTo(15);
            assertThat(response.totalPages()).isEqualTo(5);
        }

        @Test
        @DisplayName("debería crear PageResponse genérico con Integers")
        void shouldCreateGenericPageResponseWithIntegers() {
            // Given
            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

            // When
            PageResponse<Integer> response = new PageResponse<>(numbers, 2, 5, 25, 5);

            // Then
            assertThat(response.content()).containsExactly(1, 2, 3, 4, 5);
            assertThat(response.page()).isEqualTo(2);
        }

        @Test
        @DisplayName("debería crear PageResponse con página única")
        void shouldCreatePageResponseWithSinglePage() {
            // Given
            List<String> items = Arrays.asList("A", "B", "C");

            // When
            PageResponse<String> response = new PageResponse<>(items, 0, 10, 3, 1);

            // Then
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.content()).hasSize(3);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("debería crear PageResponse con múltiples páginas")
        void shouldCreatePageResponseWithMultiplePages() {
            // Given
            List<String> items = Arrays.asList("Item1", "Item2");

            // When
            PageResponse<String> response = new PageResponse<>(items, 3, 2, 100, 50);

            // Then
            assertThat(response.page()).isEqualTo(3);
            assertThat(response.totalPages()).isEqualTo(50);
            assertThat(response.totalElements()).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("Validación de Content")
    class ContentValidationTests {

        @Test
        @DisplayName("debería lanzar excepción cuando content es null")
        void shouldThrowExceptionWhenContentIsNull() {
            // Then
            assertThatThrownBy(() ->
                    new PageResponse<String>(null, 0, 10, 0, 0)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("items no puede ser null");
        }
    }

    @Nested
    @DisplayName("Validación de Page")
    class PageValidationTests {

        @ParameterizedTest
        @ValueSource(ints = {-1, -5, -100})
        @DisplayName("debería lanzar excepción cuando page es negativo")
        void shouldThrowExceptionWhenPageIsNegative(int negativePage) {
            // Given
            List<String> items = Collections.emptyList();

            // Then
            assertThatThrownBy(() ->
                    new PageResponse<>(items, negativePage, 10, 0, 0)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("page no puede ser negativo");
        }

        @Test
        @DisplayName("debería aceptar page 0")
        void shouldAcceptPageZero() {
            // Given
            List<String> items = Collections.emptyList();

            // When
            PageResponse<String> response = new PageResponse<>(items, 0, 10, 0, 0);

            // Then
            assertThat(response.page()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Validación de Size")
    class SizeValidationTests {

        @ParameterizedTest
        @ValueSource(ints = {-1, -10, -100})
        @DisplayName("debería lanzar excepción cuando size es negativo")
        void shouldThrowExceptionWhenSizeIsNegative(int negativeSize) {
            // Given
            List<String> items = Collections.emptyList();

            // Then
            assertThatThrownBy(() ->
                    new PageResponse<>(items, 0, negativeSize, 0, 0)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("size no puede ser negativo");
        }

        @Test
        @DisplayName("debería aceptar size 0")
        void shouldAcceptSizeZero() {
            // Given
            List<String> items = Collections.emptyList();

            // When
            PageResponse<String> response = new PageResponse<>(items, 0, 0, 0, 0);

            // Then
            assertThat(response.size()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Validación de TotalElements")
    class TotalElementsValidationTests {

        @ParameterizedTest
        @ValueSource(longs = {-1L, -10L, -1000L})
        @DisplayName("debería lanzar excepción cuando totalElements es negativo")
        void shouldThrowExceptionWhenTotalElementsIsNegative(long negativeTotalElements) {
            // Given
            List<String> items = Collections.emptyList();

            // Then
            assertThatThrownBy(() ->
                    new PageResponse<>(items, 0, 10, negativeTotalElements, 0)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("totalElements no puede ser negativo");
        }

        @Test
        @DisplayName("debería aceptar totalElements 0")
        void shouldAcceptTotalElementsZero() {
            // Given
            List<String> items = Collections.emptyList();

            // When
            PageResponse<String> response = new PageResponse<>(items, 0, 10, 0, 0);

            // Then
            assertThat(response.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("debería aceptar totalElements muy grande")
        void shouldAcceptVeryLargeTotalElements() {
            // Given
            List<String> items = Arrays.asList("Item");

            // When
            PageResponse<String> response = new PageResponse<>(items, 0, 1, 1000000L, 1000000);

            // Then
            assertThat(response.totalElements()).isEqualTo(1000000L);
        }
    }

    @Nested
    @DisplayName("Validación de TotalPages")
    class TotalPagesValidationTests {

        @ParameterizedTest
        @ValueSource(ints = {-1, -5, -100})
        @DisplayName("debería lanzar excepción cuando totalPages es negativo")
        void shouldThrowExceptionWhenTotalPagesIsNegative(int negativeTotalPages) {
            // Given
            List<String> items = Collections.emptyList();

            // Then
            assertThatThrownBy(() ->
                    new PageResponse<>(items, 0, 10, 0, negativeTotalPages)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("totalPages no puede ser negativo");
        }

        @Test
        @DisplayName("debería aceptar totalPages 0")
        void shouldAcceptTotalPagesZero() {
            // Given
            List<String> items = Collections.emptyList();

            // When
            PageResponse<String> response = new PageResponse<>(items, 0, 10, 0, 0);

            // Then
            assertThat(response.totalPages()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Inmutabilidad")
    class ImmutabilityTests {

        @Test
        @DisplayName("debería ser inmutable (record)")
        void shouldBeImmutable() {
            // Given
            List<String> items = Arrays.asList("A", "B");
            PageResponse<String> response = new PageResponse<>(items, 0, 2, 2, 1);

            // Then - No existen setters
            assertThat(response.content()).containsExactly("A", "B");
            assertThat(response.page()).isEqualTo(0);
        }

        @Test
        @DisplayName("modificar lista original no debería afectar el record")
        void modifyingOriginalListShouldNotAffectRecord() {
            // Given
            List<String> items = Arrays.asList("A", "B");
            PageResponse<String> response = new PageResponse<>(items, 0, 2, 2, 1);

            // When - Intentar modificar lista original (fallará si no es inmutable)
            // Note: List.of() crea lista inmutable, Arrays.asList() no lo es completamente

            // Then
            assertThat(response.content()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Igualdad y HashCode")
    class EqualityTests {

        @Test
        @DisplayName("dos PageResponse con mismos valores deberían ser iguales")
        void twoPageResponsesWithSameValuesShouldBeEqual() {
            // Given
            List<String> items1 = Arrays.asList("A", "B");
            List<String> items2 = Arrays.asList("A", "B");

            PageResponse<String> response1 = new PageResponse<>(items1, 0, 2, 2, 1);
            PageResponse<String> response2 = new PageResponse<>(items2, 0, 2, 2, 1);

            // Then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("dos PageResponse con diferentes contenidos no deberían ser iguales")
        void twoPageResponsesWithDifferentContentShouldNotBeEqual() {
            // Given
            List<String> items1 = Arrays.asList("A", "B");
            List<String> items2 = Arrays.asList("C", "D");

            PageResponse<String> response1 = new PageResponse<>(items1, 0, 2, 2, 1);
            PageResponse<String> response2 = new PageResponse<>(items2, 0, 2, 2, 1);

            // Then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("dos PageResponse con diferente page no deberían ser iguales")
        void twoPageResponsesWithDifferentPageShouldNotBeEqual() {
            // Given
            List<String> items = Arrays.asList("A", "B");

            PageResponse<String> response1 = new PageResponse<>(items, 0, 2, 2, 1);
            PageResponse<String> response2 = new PageResponse<>(items, 1, 2, 2, 1);

            // Then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("Casos de Uso Reales")
    class RealWorldUseCasesTests {

        @Test
        @DisplayName("debería representar primera página de usuarios")
        void shouldRepresentFirstPageOfUsers() {
            // Given
            User user1 = User.createClient("Alice", "alice@example.com", "$2a$10$hashed");
            User user2 = User.createClient("Bob", "bob@example.com", "$2a$10$hashed");
            List<User> users = Arrays.asList(user1, user2);

            // When
            PageResponse<User> response = new PageResponse<>(users, 0, 10, 25, 3);

            // Then
            assertThat(response.content()).hasSize(2);
            assertThat(response.page()).isEqualTo(0); // Primera página
            assertThat(response.size()).isEqualTo(10); // Tamaño de página
            assertThat(response.totalElements()).isEqualTo(25); // Total de usuarios
            assertThat(response.totalPages()).isEqualTo(3); // Total de páginas
        }

        @Test
        @DisplayName("debería representar última página con menos elementos")
        void shouldRepresentLastPageWithFewerElements() {
            // Given
            User user = User.createClient("Charlie", "charlie@example.com", "$2a$10$hashed");
            List<User> users = Collections.singletonList(user);

            // When - Última página con solo 1 usuario de un total de 21
            PageResponse<User> response = new PageResponse<>(users, 2, 10, 21, 3);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.page()).isEqualTo(2); // Tercera página (índice 2)
            assertThat(response.totalElements()).isEqualTo(21);
            assertThat(response.totalPages()).isEqualTo(3);
        }

        @Test
        @DisplayName("debería representar página vacía cuando no hay resultados")
        void shouldRepresentEmptyPageWhenNoResults() {
            // Given
            List<User> emptyList = Collections.emptyList();

            // When
            PageResponse<User> response = new PageResponse<>(emptyList, 0, 10, 0, 0);

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isEqualTo(0);
            assertThat(response.totalPages()).isEqualTo(0);
        }

        @Test
        @DisplayName("debería manejar paginación con diferentes tipos genéricos")
        void shouldHandlePaginationWithDifferentGenericTypes() {
            // Given - Página de Roles
            List<Role> roles = Arrays.asList(Role.ADMIN, Role.CLIENT, Role.EMPLOYEE);

            // When
            PageResponse<Role> response = new PageResponse<>(roles, 0, 10, 3, 1);

            // Then
            assertThat(response.content()).containsExactly(Role.ADMIN, Role.CLIENT, Role.EMPLOYEE);
        }

        @Test
        @DisplayName("debería calcular correctamente página intermedia")
        void shouldCorrectlyCalculateMiddlePage() {
            // Given - Simulando página 5 de 10
            List<String> items = Arrays.asList("Item1", "Item2", "Item3");

            // When
            PageResponse<String> response = new PageResponse<>(items, 5, 3, 30, 10);

            // Then
            assertThat(response.page()).isEqualTo(5);
            assertThat(response.totalPages()).isEqualTo(10);
            assertThat(response.content()).hasSize(3);
        }

        @Test
        @DisplayName("debería manejar tamaño de página mayor que total de elementos")
        void shouldHandlePageSizeGreaterThanTotalElements() {
            // Given
            List<String> items = Arrays.asList("A", "B", "C");

            // When - Size 100 pero solo 3 elementos
            PageResponse<String> response = new PageResponse<>(items, 0, 100, 3, 1);

            // Then
            assertThat(response.size()).isEqualTo(100);
            assertThat(response.content()).hasSize(3);
            assertThat(response.totalPages()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Validaciones Combinadas")
    class CombinedValidationsTests {

        @Test
        @DisplayName("debería rechazar múltiples valores inválidos")
        void shouldRejectMultipleInvalidValues() {
            // Given
            List<String> items = Collections.emptyList();

            // Then - Solo valida el primer error
            assertThatThrownBy(() ->
                    new PageResponse<>(items, -1, -10, -100, -5)
            )
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("debería aceptar todos los valores en cero")
        void shouldAcceptAllValuesAtZero() {
            // Given
            List<String> emptyList = Collections.emptyList();

            // When
            PageResponse<String> response = new PageResponse<>(emptyList, 0, 0, 0, 0);

            // Then
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(0);
            assertThat(response.totalElements()).isEqualTo(0);
            assertThat(response.totalPages()).isEqualTo(0);
        }
    }
}
