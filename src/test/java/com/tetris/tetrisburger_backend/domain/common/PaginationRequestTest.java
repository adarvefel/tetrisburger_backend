package com.tetris.tetrisburger_backend.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas de PaginationRequest")
class PaginationRequestTest {

    @Nested
    @DisplayName("Constructor por Defecto")
    class DefaultConstructorTests {

        @Test
        @DisplayName("debería crear con valores por defecto")
        void shouldCreateWithDefaultValues() {
            // When
            PaginationRequest request = new PaginationRequest();

            // Then
            assertThat(request.getPage()).isEqualTo(0);
            assertThat(request.getSize()).isEqualTo(12);
            assertThat(request.getSortBy()).isNull();
            assertThat(request.getDirection()).isEqualTo("ASC");
        }
    }

    @Nested
    @DisplayName("Constructor con 3 Parámetros")
    class ThreeParamConstructorTests {

        @Test
        @DisplayName("debería crear con page, size y sortBy")
        void shouldCreateWithPageSizeAndSortBy() {
            // When
            PaginationRequest request = new PaginationRequest(2, 20, "userName");

            // Then
            assertThat(request.getPage()).isEqualTo(2);
            assertThat(request.getSize()).isEqualTo(20);
            assertThat(request.getSortBy()).isEqualTo("userName");
            assertThat(request.getDirection()).isEqualTo("ASC"); // Default
        }
    }

    @Nested
    @DisplayName("Constructor Completo")
    class FullConstructorTests {

        @Test
        @DisplayName("debería crear con todos los parámetros")
        void shouldCreateWithAllParameters() {
            // When
            PaginationRequest request = new PaginationRequest(3, 25, "email", "DESC");

            // Then
            assertThat(request.getPage()).isEqualTo(3);
            assertThat(request.getSize()).isEqualTo(25);
            assertThat(request.getSortBy()).isEqualTo("email");
            assertThat(request.getDirection()).isEqualTo("DESC");
        }
    }

    @Nested
    @DisplayName("Validación de Page")
    class PageValidationTests {

        @Test
        @DisplayName("debería aceptar page válido")
        void shouldAcceptValidPage() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setPage(5);

            // Then
            assertThat(request.getPage()).isEqualTo(5);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -10, -100})
        @DisplayName("debería convertir page negativo a 0")
        void shouldConvertNegativePageToZero(int negativePage) {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setPage(negativePage);

            // Then
            assertThat(request.getPage()).isEqualTo(0);
        }

        @Test
        @DisplayName("debería mantener page 0")
        void shouldKeepPageZero() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setPage(0);

            // Then
            assertThat(request.getPage()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Validación de Size")
    class SizeValidationTests {

        @Test
        @DisplayName("debería aceptar size válido")
        void shouldAcceptValidSize() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setSize(25);

            // Then
            assertThat(request.getSize()).isEqualTo(25);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10})
        @DisplayName("debería convertir size <= 0 a 12")
        void shouldConvertInvalidSizeToTwelve(int invalidSize) {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setSize(invalidSize);

            // Then
            assertThat(request.getSize()).isEqualTo(12);
        }

        @Test
        @DisplayName("debería limitar size a máximo 100")
        void shouldLimitSizeToMaximum100() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setSize(150);

            // Then
            assertThat(request.getSize()).isEqualTo(100);
        }

        @Test
        @DisplayName("debería aceptar size de exactamente 100")
        void shouldAcceptSizeOfExactly100() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setSize(100);

            // Then
            assertThat(request.getSize()).isEqualTo(100);
        }

        @Test
        @DisplayName("debería aceptar size de 1")
        void shouldAcceptSizeOfOne() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setSize(1);

            // Then
            assertThat(request.getSize()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Validación de SortBy")
    class SortByValidationTests {

        @Test
        @DisplayName("debería aceptar sortBy válido")
        void shouldAcceptValidSortBy() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setSortBy("userName");

            // Then
            assertThat(request.getSortBy()).isEqualTo("userName");
        }

        @Test
        @DisplayName("debería convertir null a null")
        void shouldConvertNullToNull() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setSortBy(null);

            // Then
            assertThat(request.getSortBy()).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"  ", "\t", "\n", ""})
        @DisplayName("debería convertir string vacío a null")
        void shouldConvertBlankStringToNull(String blankString) {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setSortBy(blankString);

            // Then
            assertThat(request.getSortBy()).isNull();
        }
    }

    @Nested
    @DisplayName("Validación de Direction")
    class DirectionValidationTests {

        @Test
        @DisplayName("debería aceptar ASC")
        void shouldAcceptASC() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setDirection("ASC");

            // Then
            assertThat(request.getDirection()).isEqualTo("ASC");
        }

        @Test
        @DisplayName("debería aceptar DESC")
        void shouldAcceptDESC() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setDirection("DESC");

            // Then
            assertThat(request.getDirection()).isEqualTo("DESC");
        }

        @ParameterizedTest
        @ValueSource(strings = {"desc", "Desc", "DeSc", "DESC"})
        @DisplayName("debería normalizar DESC ignorando case")
        void shouldNormalizeDESCIgnoringCase(String descVariation) {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setDirection(descVariation);

            // Then
            assertThat(request.getDirection()).isEqualTo("DESC");
        }

        @ParameterizedTest
        @ValueSource(strings = {"asc", "Asc", "ASc", "ASC", "invalid", "xyz"})
        @DisplayName("debería convertir cualquier valor no-DESC a ASC")
        void shouldConvertAnyNonDESCToASC(String value) {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setDirection(value);

            // Then
            assertThat(request.getDirection()).isEqualTo("ASC");
        }

        @Test
        @DisplayName("debería convertir null a ASC")
        void shouldConvertNullToASC() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setDirection(null);

            // Then
            assertThat(request.getDirection()).isEqualTo("ASC");
        }

        @Test
        @DisplayName("debería convertir string vacío a ASC")
        void shouldConvertEmptyStringToASC() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setDirection("   ");

            // Then
            assertThat(request.getDirection()).isEqualTo("ASC");
        }
    }

    @Nested
    @DisplayName("Casos de Uso Reales")
    class RealWorldUseCasesTests {

        @Test
        @DisplayName("debería crear paginación para primera página")
        void shouldCreatePaginationForFirstPage() {
            // When
            PaginationRequest request = new PaginationRequest(0, 10, "createdAt", "DESC");

            // Then
            assertThat(request.getPage()).isEqualTo(0);
            assertThat(request.getSize()).isEqualTo(10);
            assertThat(request.getSortBy()).isEqualTo("createdAt");
            assertThat(request.getDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("debería crear paginación sin ordenamiento")
        void shouldCreatePaginationWithoutSorting() {
            // When
            PaginationRequest request = new PaginationRequest(1, 20, null);

            // Then
            assertThat(request.getPage()).isEqualTo(1);
            assertThat(request.getSize()).isEqualTo(20);
            assertThat(request.getSortBy()).isNull();
            assertThat(request.getDirection()).isEqualTo("ASC");
        }

        @Test
        @DisplayName("debería manejar solicitud de página muy alta")
        void shouldHandleVeryHighPageRequest() {
            // When
            PaginationRequest request = new PaginationRequest();
            request.setPage(9999);

            // Then
            assertThat(request.getPage()).isEqualTo(9999);
        }
    }
}
