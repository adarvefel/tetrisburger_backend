package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.enums.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas de ProductType")
class ProductTypeTest {

    @Nested
    @DisplayName("Valores del Enum")
    class EnumValuesTests {

        @Test
        @DisplayName("debería tener exactamente 3 valores")
        void deberiaTenerTresValores() {
            // When
            ProductType[] valores = ProductType.values();

            // Then
            assertThat(valores).hasSize(3);
            assertThat(valores).containsExactly(
                    ProductType.INGREDIENT,
                    ProductType.BEVERAGE,
                    ProductType.SIDE
            );
        }

        @Test
        @DisplayName("debería tener el valor INGREDIENT")
        void deberiaTenerValorIngredient() {
            // Then
            assertThat(ProductType.INGREDIENT).isNotNull();
            assertThat(ProductType.INGREDIENT.name()).isEqualTo("INGREDIENT");
        }

        @Test
        @DisplayName("debería tener el valor BEVERAGE")
        void deberiaTenerValorBeverage() {
            // Then
            assertThat(ProductType.BEVERAGE).isNotNull();
            assertThat(ProductType.BEVERAGE.name()).isEqualTo("BEVERAGE");
        }

        @Test
        @DisplayName("debería tener el valor SIDE")
        void deberiaTenerValorSide() {
            // Then
            assertThat(ProductType.SIDE).isNotNull();
            assertThat(ProductType.SIDE.name()).isEqualTo("SIDE");
        }

        @Test
        @DisplayName("debería mantener el orden de declaración")
        void deberiaMantenerOrdenDeDeclaracion() {
            // When
            ProductType[] valores = ProductType.values();

            // Then
            assertThat(valores[0]).isEqualTo(ProductType.INGREDIENT);
            assertThat(valores[1]).isEqualTo(ProductType.BEVERAGE);
            assertThat(valores[2]).isEqualTo(ProductType.SIDE);
        }

        @Test
        @DisplayName("debería tener ordinal correcto")
        void deberiaTenerOrdinalCorrecto() {
            // Then
            assertThat(ProductType.INGREDIENT.ordinal()).isEqualTo(0);
            assertThat(ProductType.BEVERAGE.ordinal()).isEqualTo(1);
            assertThat(ProductType.SIDE.ordinal()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Conversión String a Enum")
    class ConversionStringTests {

        @Test
        @DisplayName("debería convertir string a enum usando valueOf")
        void deberiaConvertirStringAEnumUsandoValueOf() {
            // Then
            assertThat(ProductType.valueOf("INGREDIENT")).isEqualTo(ProductType.INGREDIENT);
            assertThat(ProductType.valueOf("BEVERAGE")).isEqualTo(ProductType.BEVERAGE);
            assertThat(ProductType.valueOf("SIDE")).isEqualTo(ProductType.SIDE);
        }

        @Test
        @DisplayName("debería lanzar excepción con valor inválido")
        void deberiaLanzarExcepcionConValorInvalido() {
            // Then
            assertThatThrownBy(() -> ProductType.valueOf("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("debería lanzar excepción con string en minúsculas")
        void deberiaLanzarExcepcionConStringMinusculas() {
            // Then
            assertThatThrownBy(() -> ProductType.valueOf("ingredient"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("debería lanzar excepción con string null")
        void deberiaLanzarExcepcionConStringNull() {
            // Then
            assertThatThrownBy(() -> ProductType.valueOf(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Comparaciones y Igualdad")
    class ComparacionesTests {

        @Test
        @DisplayName("debería comparar tipos usando equals")
        void deberiaCompararTiposUsandoEquals() {
            // Given
            ProductType tipo1 = ProductType.INGREDIENT;
            ProductType tipo2 = ProductType.INGREDIENT;
            ProductType tipo3 = ProductType.BEVERAGE;

            // Then
            assertThat(tipo1).isEqualTo(tipo2);
            assertThat(tipo1).isNotEqualTo(tipo3);
        }

        @Test
        @DisplayName("debería comparar tipos usando operador de igualdad")
        void deberiaCompararTiposUsandoOperadorIgualdad() {
            // Given
            ProductType tipo1 = ProductType.SIDE;
            ProductType tipo2 = ProductType.SIDE;

            // Then
            assertThat(tipo1 == tipo2).isTrue();
        }

        @Test
        @DisplayName("debería ser diferente de null")
        void deberiaSerDiferenteDeNull() {
            // Given
            ProductType tipo = ProductType.INGREDIENT;

            // Then
            assertThat(tipo).isNotNull();
            assertThat(tipo.equals(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Casos de Uso del Dominio")
    class CasosDeUsoTests {

        @Test
        @DisplayName("debería identificar ingredientes para hamburguesas")
        void deberiaIdentificarIngredientesParaHamburguesas() {
            // Given
            ProductType tipo = ProductType.INGREDIENT;

            // Then
            assertThat(tipo).isEqualTo(ProductType.INGREDIENT);
            assertThat(tipo.name()).isEqualTo("INGREDIENT");
        }

        @Test
        @DisplayName("debería identificar bebidas")
        void deberiaIdentificarBebidas() {
            // Given
            ProductType tipo = ProductType.BEVERAGE;

            // Then
            assertThat(tipo).isEqualTo(ProductType.BEVERAGE);
            assertThat(tipo.name()).isEqualTo("BEVERAGE");
        }

        @Test
        @DisplayName("debería identificar acompañamientos")
        void deberiaIdentificarAcompanamientos() {
            // Given
            ProductType tipo = ProductType.SIDE;

            // Then
            assertThat(tipo).isEqualTo(ProductType.SIDE);
            assertThat(tipo.name()).isEqualTo("SIDE");
        }

        @Test
        @DisplayName("debería poder usar en switch statement")
        void deberiaPoderUsarEnSwitchStatement() {
            // Given
            ProductType tipo = ProductType.BEVERAGE;

            // When
            String categoria = switch (tipo) {
                case INGREDIENT -> "Ingrediente";
                case BEVERAGE -> "Bebida";
                case SIDE -> "Acompañamiento";
            };

            // Then
            assertThat(categoria).isEqualTo("Bebida");
        }

        @Test
        @DisplayName("debería poder filtrar productos por tipo")
        void deberiaPoderFiltrarProductosPorTipo() {
            // Given
            ProductType tipoFiltro = ProductType.INGREDIENT;

            // When
            boolean esIngrediente = tipoFiltro == ProductType.INGREDIENT;
            boolean esBebida = tipoFiltro == ProductType.BEVERAGE;

            // Then
            assertThat(esIngrediente).isTrue();
            assertThat(esBebida).isFalse();
        }
    }

    @Nested
    @DisplayName("Integración con Lógica de Negocio")
    class IntegracionLogicaNegocioTests {

        @Test
        @DisplayName("debería validar que ingredientes pueden usarse en burgers")
        void deberiaValidarIngredientesParaBurgers() {
            // Given
            ProductType tipo = ProductType.INGREDIENT;

            // When
            boolean puedeUsarseEnBurger = tipo == ProductType.INGREDIENT;

            // Then
            assertThat(puedeUsarseEnBurger).isTrue();
        }

        @Test
        @DisplayName("debería validar que bebidas NO pueden usarse en burgers")
        void deberiaValidarBebidasNoPuedenUsarseEnBurgers() {
            // Given
            ProductType tipo = ProductType.BEVERAGE;

            // When
            boolean puedeUsarseEnBurger = tipo == ProductType.INGREDIENT;

            // Then
            assertThat(puedeUsarseEnBurger).isFalse();
        }

        @Test
        @DisplayName("debería validar que sides NO pueden usarse en burgers")
        void deberiaValidarSidesNoPuedenUsarseEnBurgers() {
            // Given
            ProductType tipo = ProductType.SIDE;

            // When
            boolean puedeUsarseEnBurger = tipo == ProductType.INGREDIENT;

            // Then
            assertThat(puedeUsarseEnBurger).isFalse();
        }

        @Test
        @DisplayName("debería categorizar correctamente múltiples productos")
        void deberiaCategorizarCorrectamenteMultiplesProductos() {
            // When & Then
            assertThat(ProductType.INGREDIENT).isNotEqualTo(ProductType.BEVERAGE);
            assertThat(ProductType.BEVERAGE).isNotEqualTo(ProductType.SIDE);
            assertThat(ProductType.SIDE).isNotEqualTo(ProductType.INGREDIENT);
        }
    }

    @Nested
    @DisplayName("Serialización y Conversión")
    class SerializacionTests {

        @Test
        @DisplayName("debería convertir a String usando name()")
        void deberiaConvertirAStringUsandoName() {
            // Given
            ProductType tipo = ProductType.INGREDIENT;

            // When
            String nombre = tipo.name();

            // Then
            assertThat(nombre).isEqualTo("INGREDIENT");
        }

        @Test
        @DisplayName("debería convertir a String usando toString()")
        void deberiaConvertirAStringUsandoToString() {
            // Given
            ProductType tipo = ProductType.BEVERAGE;

            // When
            String texto = tipo.toString();

            // Then
            assertThat(texto).isEqualTo("BEVERAGE");
        }

        @Test
        @DisplayName("debería poder deserializar desde String")
        void deberiaPoderDeserializarDesdeString() {
            // Given
            String tipoString = "SIDE";

            // When
            ProductType tipo = ProductType.valueOf(tipoString);

            // Then
            assertThat(tipo).isEqualTo(ProductType.SIDE);
        }
    }

    @Nested
    @DisplayName("Validaciones de Seguridad")
    class ValidacionesSeguridadTests {

        @Test
        @DisplayName("debería rechazar tipo de producto desconocido de API")
        void deberiaRechazarTipoDesconocido() {
            // Given
            String tipoDesdeApi = "DESSERT"; // No existe

            // Then
            assertThatThrownBy(() -> ProductType.valueOf(tipoDesdeApi))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("debería manejar correctamente valores desde frontend")
        void deberiaManejarValoresDesdeFrontend() {
            // Given - Simula que frontend envía estos valores
            String[] valoresValidos = {"INGREDIENT", "BEVERAGE", "SIDE"};

            // When & Then
            for (String valor : valoresValidos) {
                assertThatCode(() -> ProductType.valueOf(valor))
                        .doesNotThrowAnyException();
            }
        }

        @Test
        @DisplayName("debería validar que todos los tipos declarados son accesibles")
        void deberiaValidarTodosLosTiposAccesibles() {
            // When
            ProductType[] tipos = ProductType.values();

            // Then
            assertThat(tipos).isNotEmpty();
            assertThat(tipos).doesNotContainNull();
            assertThat(tipos).hasSize(3);
        }
    }
}
