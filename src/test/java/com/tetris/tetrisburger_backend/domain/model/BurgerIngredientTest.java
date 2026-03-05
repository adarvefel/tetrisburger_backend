//package com.tetris.tetrisburger_backend.domain.model;
//
//import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//
//import static org.assertj.core.api.Assertions.*;
//
//@DisplayName("Pruebas de BurgerIngredient")
//class BurgerIngredientTest {
//
//    // ============================================
//    // FACTORY METHOD - reconstitute
//    // ============================================
//
//    @Nested
//    @DisplayName("Factory reconstitute")
//    class ReconstituteTests {
//
//        @Test
//        @DisplayName("debería reconstituir ingrediente desde persistencia con todos los datos")
//        void deberiaReconstiturIngredienteDesdePersistenciaConTodosLosDatos() {
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Carne de Res",
//                    new BigDecimal("5.00"),
//                    2,
//                    new BigDecimal("10.00"),
//                    true
//            );
//
//            // Then
//            assertThat(ingrediente).isNotNull();
//            assertThat(ingrediente.getIdBurgerIngredient()).isEqualTo(1);
//            assertThat(ingrediente.getIdProduct()).isEqualTo(10);
//            assertThat(ingrediente.getProductName()).isEqualTo("Carne de Res");
//            assertThat(ingrediente.getPriceAtTime()).isEqualByComparingTo("5.00");
//            assertThat(ingrediente.getQuantity()).isEqualTo(2);
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo("10.00");
//        }
//
//        @Test
//        @DisplayName("debería usar valores por defecto cuando subtotal es null")
//        void deberiaUsarValoresPorDefectoCuandoSubtotalEsNull() {
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Queso",
//                    new BigDecimal("1.50"),
//                    3,
//                    null,
//                    false
//            );
//
//            // Then
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo("0.00");
//        }
//
//        @Test
//        @DisplayName("debería usar false por defecto cuando isOptional es null")
//        void deberiaUsarFalsePorDefectoCuandoIsOptionalEsNull() {
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Tomate",
//                    new BigDecimal("0.50"),
//                    1,
//                    new BigDecimal("0.50"),
//                    null
//            );
//
//            // Then
//            assertThat(ingrediente.isOptional()).isFalse();
//            assertThat(ingrediente.getIsOptional()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería reconstituir con valores mínimos")
//        void deberiaReconstiturConValoresMinimos() {
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    null,
//                    1,
//                    "Pan",
//                    new BigDecimal("1.00"),
//                    1,
//                    null,
//                    null
//            );
//
//            // Then
//            assertThat(ingrediente.getIdBurgerIngredient()).isNull();
//            assertThat(ingrediente.getIdProduct()).isEqualTo(1);
//            assertThat(ingrediente.getProductName()).isEqualTo("Pan");
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo("0.00");
//            assertThat(ingrediente.isOptional()).isFalse();
//        }
//    }
//
//    // ============================================
//    // FACTORY METHOD - fromSnapshot
//    // ============================================
//
//    @Nested
//    @DisplayName("Factory fromSnapshot")
//    class FromSnapshotTests {
//
//        @Test
//        @DisplayName("debería crear ingrediente desde snapshot válido")
//        void deberiaCrearIngredienteDesdeSnapshotValido() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    5,
//                    "Lechuga",
//                    new BigDecimal("0.75"),
//                    "Vegetales",
//                    2,
//                    true
//            );
//
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.fromSnapshot(snapshot);
//
//            // Then
//            assertThat(ingrediente).isNotNull();
//            assertThat(ingrediente.getIdProduct()).isEqualTo(5);
//            assertThat(ingrediente.getProductName()).isEqualTo("Lechuga");
//            assertThat(ingrediente.getPriceAtTime()).isEqualByComparingTo("0.75");
//            assertThat(ingrediente.getQuantity()).isEqualTo(2);
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo("1.50"); // 0.75 * 2
//            assertThat(ingrediente.isOptional()).isTrue();
//        }
//
//        @Test
//        @DisplayName("debería crear ingrediente con isOptional false")
//        void deberiaCrearIngredienteConIsOptionalFalse() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    3,
//                    "Carne",
//                    new BigDecimal("5.00"),
//                    "Carnes",
//                    1,
//                    false
//            );
//
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.fromSnapshot(snapshot);
//
//            // Then
//            assertThat(ingrediente.isOptional()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería calcular subtotal correctamente al crear desde snapshot")
//        void deberiaCalcularSubtotalCorrectamenteAlCrearDesdeSnapshot() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    7,
//                    "Bacon",
//                    new BigDecimal("2.50"),
//                    "Carnes",
//                    3,
//                    false
//            );
//
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.fromSnapshot(snapshot);
//
//            // Then
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo("7.50"); // 2.50 * 3
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando snapshot es null")
//        void deberiaLanzarExcepcionCuandoSnapshotEsNull() {
//            // Then
//            assertThatThrownBy(() ->
//                    BurgerIngredient.fromSnapshot(null)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("ProductSnapshot no puede ser null");
//        }
//
//        @Test
//        @DisplayName("debería crear ingrediente con cantidad 1")
//        void deberiaCrearIngredienteConCantidadUno() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    8,
//                    "Pan Integral",
//                    new BigDecimal("1.25"),
//                    "Panes",
//                    1,
//                    false
//            );
//
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.fromSnapshot(snapshot);
//
//            // Then
//            assertThat(ingrediente.getQuantity()).isEqualTo(1);
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo("1.25");
//        }
//    }
//
//    // ============================================
//    // COMPORTAMIENTO - calculateSubtotal
//    // ============================================
//
//    @Nested
//    @DisplayName("Cálculo de Subtotal")
//    class CalculateSubtotalTests {
//
//        @Test
//        @DisplayName("debería calcular subtotal correctamente")
//        void deberiaCalcularSubtotalCorrectamente() {
//            // Given
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Queso Cheddar",
//                    new BigDecimal("1.50"),
//                    4,
//                    null,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = ingrediente.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("6.00"); // 1.50 * 4
//        }
//
//        @Test
//        @DisplayName("debería calcular subtotal con cantidad 1")
//        void deberiaCalcularSubtotalConCantidadUno() {
//            // Given
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Tomate",
//                    new BigDecimal("0.50"),
//                    1,
//                    null,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = ingrediente.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("0.50");
//        }
//
//        @Test
//        @DisplayName("debería retornar cero cuando precio es null")
//        void deberiaRetornarCeroCuandoPrecioEsNull() {
//            // Given
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Producto",
//                    null,
//                    2,
//                    null,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = ingrediente.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("0.00");
//        }
//
//        @Test
//        @DisplayName("debería calcular subtotal con precio decimal")
//        void deberiaCalcularSubtotalConPrecioDecimal() {
//            // Given
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Salsa Especial",
//                    new BigDecimal("0.25"),
//                    8,
//                    null,
//                    true
//            );
//
//            // When
//            BigDecimal subtotal = ingrediente.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("2.00"); // 0.25 * 8
//        }
//    }
//
//    // ============================================
//    // GETTERS
//    // ============================================
//
//    @Nested
//    @DisplayName("Métodos Getter")
//    class GetterTests {
//
//        @Test
//        @DisplayName("debería retornar subtotal almacenado cuando existe")
//        void deberiaRetornarSubtotalAlmacenadoCuandoExiste() {
//            // Given
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Carne",
//                    new BigDecimal("5.00"),
//                    2,
//                    new BigDecimal("10.00"),
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = ingrediente.getSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("10.00");
//        }
//
//        @Test
//        @DisplayName("debería calcular subtotal cuando no está almacenado")
//        void deberiaCalcularSubtotalCuandoNoEstaAlmacenado() {
//            // Given
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    1,
//                    10,
//                    "Queso",
//                    new BigDecimal("2.00"),
//                    3,
//                    null,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = ingrediente.getSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("6.00"); // Calculado: 2.00 * 3
//        }
//
//        @Test
//        @DisplayName("debería retornar todos los valores correctamente")
//        void deberiaRetornarTodosLosValoresCorrectamente() {
//            // Given
//            BurgerIngredient ingrediente = BurgerIngredient.reconstitute(
//                    100,
//                    50,
//                    "Pepinillo",
//                    new BigDecimal("0.30"),
//                    5,
//                    new BigDecimal("1.50"),
//                    true
//            );
//
//            // Then
//            assertThat(ingrediente.getIdBurgerIngredient()).isEqualTo(100);
//            assertThat(ingrediente.getIdProduct()).isEqualTo(50);
//            assertThat(ingrediente.getProductName()).isEqualTo("Pepinillo");
//            assertThat(ingrediente.getPriceAtTime()).isEqualByComparingTo("0.30");
//            assertThat(ingrediente.getQuantity()).isEqualTo(5);
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo("1.50");
//            assertThat(ingrediente.isOptional()).isTrue();
//            assertThat(ingrediente.getIsOptional()).isTrue();
//        }
//    }
//
//    // ============================================
//    // SETTERS (para infraestructura)
//    // ============================================
//
//    @Nested
//    @DisplayName("Métodos Setter")
//    class SetterTests {
//
//        @Test
//        @DisplayName("debería establecer idBurgerIngredient")
//        void deberiaEstablecerIdBurgerIngredient() {
//            // Given
//            BurgerIngredient ingrediente = crearIngredienteBasico();
//
//            // When
//            ingrediente.setIdBurgerIngredient(999);
//
//            // Then
//            assertThat(ingrediente.getIdBurgerIngredient()).isEqualTo(999);
//        }
//
//        @Test
//        @DisplayName("debería establecer idProduct")
//        void deberiaEstablecerIdProduct() {
//            // Given
//            BurgerIngredient ingrediente = crearIngredienteBasico();
//
//            // When
//            ingrediente.setIdProduct(777);
//
//            // Then
//            assertThat(ingrediente.getIdProduct()).isEqualTo(777);
//        }
//
//        @Test
//        @DisplayName("debería establecer productName")
//        void deberiaEstablecerProductName() {
//            // Given
//            BurgerIngredient ingrediente = crearIngredienteBasico();
//
//            // When
//            ingrediente.setProductName("Nuevo Nombre");
//
//            // Then
//            assertThat(ingrediente.getProductName()).isEqualTo("Nuevo Nombre");
//        }
//
//        @Test
//        @DisplayName("debería establecer priceAtTime")
//        void deberiaEstablecerPriceAtTime() {
//            // Given
//            BurgerIngredient ingrediente = crearIngredienteBasico();
//
//            // When
//            ingrediente.setPriceAtTime(new BigDecimal("99.99"));
//
//            // Then
//            assertThat(ingrediente.getPriceAtTime()).isEqualByComparingTo("99.99");
//        }
//
//        @Test
//        @DisplayName("debería establecer quantity")
//        void deberiaEstablecerQuantity() {
//            // Given
//            BurgerIngredient ingrediente = crearIngredienteBasico();
//
//            // When
//            ingrediente.setQuantity(10);
//
//            // Then
//            assertThat(ingrediente.getQuantity()).isEqualTo(10);
//        }
//
//        @Test
//        @DisplayName("debería establecer subtotal")
//        void deberiaEstablecerSubtotal() {
//            // Given
//            BurgerIngredient ingrediente = crearIngredienteBasico();
//
//            // When
//            ingrediente.setSubtotal(new BigDecimal("25.00"));
//
//            // Then
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo("25.00");
//        }
//
//        @Test
//        @DisplayName("debería establecer isOptional")
//        void deberiaEstablecerIsOptional() {
//            // Given
//            BurgerIngredient ingrediente = crearIngredienteBasico();
//            assertThat(ingrediente.isOptional()).isFalse();
//
//            // When
//            ingrediente.setOptional(true);
//
//            // Then
//            assertThat(ingrediente.isOptional()).isTrue();
//            assertThat(ingrediente.getIsOptional()).isTrue();
//        }
//    }
//
//    // ============================================
//    // INTEGRACIÓN: Snapshot a Ingrediente
//    // ============================================
//
//    @Nested
//    @DisplayName("Integración Snapshot a Ingrediente")
//    class IntegrationTests {
//
//        @Test
//        @DisplayName("debería mantener consistencia al crear desde snapshot")
//        void deberiaMantenerConsistenciaAlCrearDesdeSnapshot() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    15,
//                    "Cebolla Caramelizada",
//                    new BigDecimal("1.75"),
//                    "Vegetales",
//                    2,
//                    true
//            );
//
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.fromSnapshot(snapshot);
//
//            // Then
//            assertThat(ingrediente.getIdProduct()).isEqualTo(snapshot.idProduct());
//            assertThat(ingrediente.getProductName()).isEqualTo(snapshot.name());
//            assertThat(ingrediente.getPriceAtTime()).isEqualByComparingTo(snapshot.price());
//            assertThat(ingrediente.getQuantity()).isEqualTo(snapshot.quantity());
//            assertThat(ingrediente.isOptional()).isEqualTo(snapshot.isOptional());
//            assertThat(ingrediente.getSubtotal()).isEqualByComparingTo(snapshot.calculateSubtotal());
//        }
//
//        @Test
//        @DisplayName("debería calcular mismo subtotal que snapshot")
//        void deberiaCalcularMismoSubtotalQueSnapshot() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    20,
//                    "Aguacate",
//                    new BigDecimal("3.00"),
//                    "Vegetales",
//                    1,
//                    true
//            );
//
//            // When
//            BurgerIngredient ingrediente = BurgerIngredient.fromSnapshot(snapshot);
//
//            // Then
//            assertThat(ingrediente.calculateSubtotal())
//                    .isEqualByComparingTo(snapshot.calculateSubtotal());
//            assertThat(ingrediente.getSubtotal())
//                    .isEqualByComparingTo(snapshot.calculateSubtotal());
//        }
//    }
//
//    // ============================================
//    // MÉTODOS DE AYUDA
//    // ============================================
//
//    private BurgerIngredient crearIngredienteBasico() {
//        return BurgerIngredient.reconstitute(
//                1,
//                10,
//                "Ingrediente Básico",
//                new BigDecimal("1.00"),
//                1,
//                new BigDecimal("1.00"),
//                false
//        );
//    }
//}
