//package com.tetris.tetrisburger_backend.domain.model;
//
//import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//@DisplayName("Pruebas del Agregado Burger")
//class BurgerTest {
//
//    // ============================================
//    // MÉTODOS FACTORY - Menu Burger Simple
//    // ============================================
//
//    @Nested
//    @DisplayName("Factory createMenuBurger")
//    class CreateMenuBurgerTests {
//
//        @Test
//        @DisplayName("debería crear hamburguesa de menú con datos válidos")
//        void deberiaCrearHamburguesaDeMenuExitosamente() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa Clásica", new BigDecimal("10.00"));
//
//            // Then
//            assertThat(burger).isNotNull();
//            assertThat(burger.getName()).isEqualTo("Hamburguesa Clásica");
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("10.00");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("10.00");
//            assertThat(burger.isMenuBurger()).isTrue();
//            assertThat(burger.isCustomBurger()).isFalse();
//            assertThat(burger.isFavoriteBurger()).isFalse();
//            assertThat(burger.isAvailability()).isTrue();
//            assertThat(burger.getCreatedAt()).isNotNull();
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el nombre es null")
//        void deberiaLanzarExcepcionCuandoNombreEsNull() {
//            assertThatThrownBy(() ->
//                    Burger.createMenuBurger(null, new BigDecimal("10.00"))
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("nombre es obligatorio");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el nombre está vacío")
//        void deberiaLanzarExcepcionCuandoNombreEstaVacio() {
//            assertThatThrownBy(() ->
//                    Burger.createMenuBurger("   ", new BigDecimal("10.00"))
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("nombre es obligatorio");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el precio es null")
//        void deberiaLanzarExcepcionCuandoPrecioEsNull() {
//            assertThatThrownBy(() ->
//                    Burger.createMenuBurger("Hamburguesa Clásica", null)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("precio base debe ser mayor a 0");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el precio es cero")
//        void deberiaLanzarExcepcionCuandoPrecioEsCero() {
//            assertThatThrownBy(() ->
//                    Burger.createMenuBurger("Hamburguesa Clásica", BigDecimal.ZERO)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("precio base debe ser mayor a 0");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el precio es negativo")
//        void deberiaLanzarExcepcionCuandoPrecioEsNegativo() {
//            assertThatThrownBy(() ->
//                    Burger.createMenuBurger("Hamburguesa Clásica", new BigDecimal("-5.00"))
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("precio base debe ser mayor a 0");
//        }
//    }
//
//    // ============================================
//    // MÉTODOS FACTORY - Menu Burger con Ingredientes
//    // ============================================
//
//    @Nested
//    @DisplayName("Factory menuBurger con Ingredientes")
//    class MenuBurgerConIngredientesTests {
//
//        @Test
//        @DisplayName("debería crear hamburguesa de menú con ingredientes y calcular precio")
//        void deberiaCrearHamburguesaDeMenuConIngredientes() {
//            // Given
//            List<ProductSnapshot> ingredients = List.of(
//                    crearProductSnapshot(1, "Pan", new BigDecimal("1.50"), 1),
//                    crearProductSnapshot(2, "Carne de Res", new BigDecimal("5.00"), 1),
//                    crearProductSnapshot(3, "Queso", new BigDecimal("1.00"), 2)
//            );
//
//            // When
//            Burger burger = Burger.menuBurger(
//                    "Hamburguesa con Queso",
//                    "Deliciosa hamburguesa con queso",
//                    "burger.jpg",
//                    ingredients,
//                    true
//            );
//
//            // Then
//            assertThat(burger.getName()).isEqualTo("Hamburguesa con Queso");
//            assertThat(burger.getDescription()).isEqualTo("Deliciosa hamburguesa con queso");
//            assertThat(burger.getImageUrl()).isEqualTo("burger.jpg");
//            assertThat(burger.isMenuBurger()).isTrue();
//            assertThat(burger.isFavoriteBurger()).isTrue();
//            assertThat(burger.getIngredients()).hasSize(3);
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("8.50"); // 1.50 + 5.00 + (1.00*2)
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("8.50");
//        }
//
//        @Test
//        @DisplayName("debería crear hamburguesa de menú con lista de ingredientes vacía")
//        void deberiaCrearHamburguesaDeMenuConIngredientesVacios() {
//            // When
//            Burger burger = Burger.menuBurger(
//                    "Hamburguesa Simple",
//                    null,
//                    null,
//                    new ArrayList<>(),
//                    false
//            );
//
//            // Then
//            assertThat(burger.getIngredients()).isEmpty();
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("0.00");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("0.00");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el nombre es null")
//        void deberiaLanzarExcepcionCuandoNombreEsNull() {
//            assertThatThrownBy(() ->
//                    Burger.menuBurger(null, "desc", "image.jpg", List.of(), false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("nombre es obligatorio");
//        }
//    }
//
//    // ============================================
//    // MÉTODOS FACTORY - Custom Burger Builder
//    // ============================================
//
//    @Nested
//    @DisplayName("Factory CustomBuilder")
//    class CustomBuilderTests {
//
//        @Test
//        @DisplayName("debería crear hamburguesa personalizada con builder")
//        void deberiaCrearHamburguesaPersonalizadaConBuilder() {
//            // Given
//            ProductSnapshot ingredient1 = crearProductSnapshot(1, "Pan", new BigDecimal("2.00"), 1);
//            ProductSnapshot ingredient2 = crearProductSnapshot(2, "Pollo", new BigDecimal("4.00"), 1);
//
//            // When
//            Burger burger = new Burger.CustomBuilder("Mi Hamburguesa Personalizada", 100)
//                    .addIngredient(ingredient1)
//                    .addIngredient(ingredient2)
//                    .withDescription("Mi hamburguesa especial")
//                    .withImage("custom.jpg")
//                    .build();
//
//            // Then
//            assertThat(burger.getName()).isEqualTo("Mi Hamburguesa Personalizada");
//            assertThat(burger.getDescription()).isEqualTo("Mi hamburguesa especial");
//            assertThat(burger.getImageUrl()).isEqualTo("custom.jpg");
//            assertThat(burger.isCustomBurger()).isTrue();
//            assertThat(burger.isMenuBurger()).isFalse();
//            assertThat(burger.getIdUser()).isEqualTo(100);
//            assertThat(burger.getIngredients()).hasSize(2);
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("6.00");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("6.00");
//            assertThat(burger.getCreatedBy()).isEqualTo(100);
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el nombre es null en el builder")
//        void deberiaLanzarExcepcionCuandoNombreEsNull() {
//            assertThatThrownBy(() ->
//                    new Burger.CustomBuilder(null, 100)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("nombre es obligatorio");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el userId es null en el builder")
//        void deberiaLanzarExcepcionCuandoUserIdEsNull() {
//            assertThatThrownBy(() ->
//                    new Burger.CustomBuilder("Hamburguesa Personalizada", null)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("ID del usuario es obligatorio");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción al construir sin ingredientes")
//        void deberiaLanzarExcepcionAlConstruirSinIngredientes() {
//            assertThatThrownBy(() ->
//                    new Burger.CustomBuilder("Hamburguesa Personalizada", 100).build()
//            ).isInstanceOf(IllegalStateException.class)
//                    .hasMessageContaining("al menos un ingrediente");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción al agregar ingrediente null")
//        void deberiaLanzarExcepcionAlAgregarIngredienteNull() {
//            assertThatThrownBy(() ->
//                    new Burger.CustomBuilder("Hamburguesa Personalizada", 100)
//                            .addIngredient(null)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("snapshot no puede ser null");
//        }
//    }
//
//    // ============================================
//    // COMPORTAMIENTO HAMBURGUESA DE MENÚ
//    // ============================================
//
//    @Nested
//    @DisplayName("Comportamiento updateMenuBurger")
//    class UpdateMenuBurgerTests {
//
//        @Test
//        @DisplayName("debería actualizar hamburguesa de menú exitosamente")
//        void deberiaActualizarHamburguesaDeMenuExitosamente() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Original", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//
//            List<BurgerIngredient> nuevosIngredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", new BigDecimal("2.00"), 1)
//            );
//
//            // When
//            burger.updateMenuBurger(
//                    "Nombre Actualizado",
//                    "Descripción Actualizada",
//                    nuevosIngredientes,
//                    false,
//                    1
//            );
//
//            // Then
//            assertThat(burger.getName()).isEqualTo("Nombre Actualizado");
//            assertThat(burger.getDescription()).isEqualTo("Descripción Actualizada");
//            assertThat(burger.isAvailability()).isFalse();
//            assertThat(burger.getIngredients()).hasSize(1);
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("2.00");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("2.00");
//            assertThat(burger.getUpdatedAt()).isNotNull();
//            assertThat(burger.getUpdatedBy()).isEqualTo(1);
//        }
//
//        @Test
//        @DisplayName("debería mantener precio final personalizado al actualizar ingredientes")
//        void deberiaMantenerPrecioFinalPersonalizadoAlActualizar() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Original", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//            burger.updateFinalPrice(new BigDecimal("15.00"), 1); // Precio personalizado
//
//            List<BurgerIngredient> nuevosIngredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", new BigDecimal("2.00"), 1)
//            );
//
//            // When
//            burger.updateMenuBurger("Actualizado", "Desc", nuevosIngredientes, true, 1);
//
//            // Then
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("2.00");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("15.00"); // Mantiene precio personalizado
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción al actualizar hamburguesa que no es de menú")
//        void deberiaLanzarExcepcionAlActualizarHamburguesaQueNoEsDeMenu() {
//            // Given
//            Burger hamburguesaPersonalizada = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            hamburguesaPersonalizada.setIdBurger(1);
//
//            List<BurgerIngredient> ingredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", BigDecimal.ONE, 1)
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    hamburguesaPersonalizada.updateMenuBurger("Nombre", "Desc", ingredientes, true, 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("Solo hamburguesas del menú");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción al actualizar hamburguesa eliminada")
//        void deberiaLanzarExcepcionAlActualizarHamburguesaEliminada() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Original", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//            burger.markAsDeleted(1);
//
//            List<BurgerIngredient> ingredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", BigDecimal.ONE, 1)
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateMenuBurger("Nombre", "Desc", ingredientes, true, 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("eliminada");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el nombre está vacío")
//        void deberiaLanzarExcepcionCuandoNombreEstaVacio() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Original", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//
//            List<BurgerIngredient> ingredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", BigDecimal.ONE, 1)
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateMenuBurger("", "Desc", ingredientes, true, 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("nombre no puede estar vacío");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando los ingredientes están vacíos")
//        void deberiaLanzarExcepcionCuandoIngredientesEstanVacios() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Original", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateMenuBurger("Nombre", "Desc", new ArrayList<>(), true, 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("al menos un ingrediente");
//        }
//    }
//
//    @Nested
//    @DisplayName("Comportamiento updateFinalPrice")
//    class UpdateFinalPriceTests {
//
//        @Test
//        @DisplayName("debería actualizar precio final exitosamente")
//        void deberiaActualizarPrecioFinalExitosamente() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//
//            // When
//            burger.updateFinalPrice(new BigDecimal("15.00"), 1);
//
//            // Then
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("15.00");
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("10.00"); // Base no cambia
//            assertThat(burger.getUpdatedBy()).isEqualTo(1);
//            assertThat(burger.getUpdatedAt()).isNotNull();
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción para hamburguesa que no es de menú")
//        void deberiaLanzarExcepcionParaHamburguesaQueNoEsDeMenu() {
//            // Given
//            Burger hamburguesaPersonalizada = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//
//            // Then
//            assertThatThrownBy(() ->
//                    hamburguesaPersonalizada.updateFinalPrice(new BigDecimal("20.00"), 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("Solo hamburguesas del menú");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el precio es cero")
//        void deberiaLanzarExcepcionCuandoPrecioEsCero() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateFinalPrice(BigDecimal.ZERO, 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("precio debe ser mayor a 0");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando updatedBy es null")
//        void deberiaLanzarExcepcionCuandoUpdatedByEsNull() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateFinalPrice(new BigDecimal("15.00"), null)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("updatedBy no puede ser null");
//        }
//    }
//
//    @Nested
//    @DisplayName("Comportamiento setAvailability")
//    class SetAvailabilityTests {
//
//        @Test
//        @DisplayName("debería cambiar disponibilidad exitosamente")
//        void deberiaCambiarDisponibilidadExitosamente() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//
//            // When
//            burger.setAvailability(false, 1);
//
//            // Then
//            assertThat(burger.isAvailability()).isFalse();
//            assertThat(burger.getUpdatedBy()).isEqualTo(1);
//            assertThat(burger.getUpdatedAt()).isNotNull();
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción para hamburguesa que no es de menú")
//        void deberiaLanzarExcepcionParaHamburguesaQueNoEsDeMenu() {
//            // Given
//            Burger hamburguesaPersonalizada = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//
//            // Then
//            assertThatThrownBy(() ->
//                    hamburguesaPersonalizada.setAvailability(false, 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("Solo hamburguesas del menú");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción para hamburguesa eliminada")
//        void deberiaLanzarExcepcionParaHamburguesaEliminada() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//            burger.markAsDeleted(1);
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.setAvailability(true, 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("eliminada");
//        }
//    }
//
//    @Nested
//    @DisplayName("Comportamiento markAsDeleted")
//    class MarkAsDeletedTests {
//
//        @Test
//        @DisplayName("debería marcar hamburguesa como eliminada exitosamente")
//        void deberiaMarcarHamburguesaComoEliminadaExitosamente() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//
//            // When
//            burger.markAsDeleted(1);
//
//            // Then
//            assertThat(burger.isDeleted()).isTrue();
//            assertThat(burger.getDeletedAt()).isNotNull();
//            assertThat(burger.getDeletedBy()).isEqualTo(1);
//            assertThat(burger.isAvailability()).isFalse();
//            assertThat(burger.getUpdatedAt()).isNotNull();
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando ya está eliminada")
//        void deberiaLanzarExcepcionCuandoYaEstaEliminada() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//            burger.markAsDeleted(1);
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.markAsDeleted(1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("ya está eliminada");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando deletedBy es null")
//        void deberiaLanzarExcepcionCuandoDeletedByEsNull() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.markAsDeleted(null)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("no puede ser null");
//        }
//    }
//
//    // ============================================
//    // COMPORTAMIENTO HAMBURGUESA PERSONALIZADA
//    // ============================================
//
//    @Nested
//    @DisplayName("Comportamiento updateCustomBurger")
//    class UpdateCustomBurgerTests {
//
//        @Test
//        @DisplayName("debería actualizar hamburguesa personalizada exitosamente")
//        void deberiaActualizarHamburguesaPersonalizadaExitosamente() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada Original", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            burger.setIdBurger(1);
//
//            List<BurgerIngredient> nuevosIngredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", new BigDecimal("2.00"), 1),
//                    crearBurgerIngredient(2, "Queso", new BigDecimal("1.50"), 1)
//            );
//
//            // When
//            burger.updateCustomBurger(100, "Personalizada Actualizada", "Nueva descripción", nuevosIngredientes);
//
//            // Then
//            assertThat(burger.getName()).isEqualTo("Personalizada Actualizada");
//            assertThat(burger.getDescription()).isEqualTo("Nueva descripción");
//            assertThat(burger.getIngredients()).hasSize(2);
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("3.50");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("3.50");
//            assertThat(burger.getUpdatedAt()).isNotNull();
//            assertThat(burger.getUpdatedBy()).isEqualTo(100);
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción para hamburguesa que no es personalizada")
//        void deberiaLanzarExcepcionParaHamburguesaQueNoEsPersonalizada() {
//            // Given
//            Burger hamburguesaMenu = Burger.createMenuBurger("Menu", BigDecimal.TEN);
//            List<BurgerIngredient> ingredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", BigDecimal.ONE, 1)
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    hamburguesaMenu.updateCustomBurger(1, "Nombre", "Desc", ingredientes)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("Solo hamburguesas personalizadas");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el usuario no es el propietario")
//        void deberiaLanzarExcepcionCuandoUsuarioNoEsPropietario() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//
//            List<BurgerIngredient> ingredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", BigDecimal.ONE, 1)
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateCustomBurger(999, "Nombre", "Desc", ingredientes)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("no pertenece a este usuario");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando la hamburguesa está eliminada")
//        void deberiaLanzarExcepcionCuandoHamburguesaEstaEliminada() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            burger.setIdBurger(1);
//            burger.markCustomAsDeleted(100);
//
//            List<BurgerIngredient> ingredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", BigDecimal.ONE, 1)
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateCustomBurger(100, "Nombre", "Desc", ingredientes)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("eliminada");
//        }
//    }
//
//    @Nested
//    @DisplayName("Comportamiento markCustomAsDeleted")
//    class MarkCustomAsDeletedTests {
//
//        @Test
//        @DisplayName("debería marcar hamburguesa personalizada como eliminada exitosamente")
//        void deberiaMarcarHamburguesaPersonalizadaComoEliminadaExitosamente() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            burger.setIdBurger(1);
//
//            // When
//            burger.markCustomAsDeleted(100);
//
//            // Then
//            assertThat(burger.isDeleted()).isTrue();
//            assertThat(burger.getDeletedAt()).isNotNull();
//            assertThat(burger.getDeletedBy()).isEqualTo(100);
//            assertThat(burger.isAvailability()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción para hamburguesa que no es personalizada")
//        void deberiaLanzarExcepcionParaHamburguesaQueNoEsPersonalizada() {
//            // Given
//            Burger hamburguesaMenu = Burger.createMenuBurger("Menu", BigDecimal.TEN);
//
//            // Then
//            assertThatThrownBy(() ->
//                    hamburguesaMenu.markCustomAsDeleted(1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("Solo hamburguesas personalizadas");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando el usuario no es el propietario")
//        void deberiaLanzarExcepcionCuandoUsuarioNoEsPropietario() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.markCustomAsDeleted(999)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("no pertenece a este usuario");
//        }
//    }
//
//    // ============================================
//    // FUNCIONALIDAD DE FAVORITOS - HAMBURGUESAS DE MENÚ
//    // ============================================
//
//    @Nested
//    @DisplayName("Comportamiento de Favoritos en Hamburguesas de Menú")
//    class MenuBurgerFavoriteTests {
//
//        @Test
//        @DisplayName("debería establecer hamburguesa de menú como favorita")
//        void deberiaEstablecerHamburguesaDeMenuComoFavorita() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            burger.setIdBurger(1);
//
//            // When
//            burger.setMenuBurgerFavorite(true, 1);
//
//            // Then
//            assertThat(burger.isFavoriteBurger()).isTrue();
//            assertThat(burger.getUpdatedBy()).isEqualTo(1);
//            assertThat(burger.getUpdatedAt()).isNotNull();
//        }
//
//        @Test
//        @DisplayName("debería alternar estado de favorito en hamburguesa de menú")
//        void deberiaAlternarEstadoFavoritoEnHamburguesaDeMenu() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            burger.setIdBurger(1);
//            assertThat(burger.isFavoriteBurger()).isFalse();
//
//            // When
//            burger.toggleMenuFavorite(1);
//
//            // Then
//            assertThat(burger.isFavoriteBurger()).isTrue();
//
//            // When
//            burger.toggleMenuFavorite(1);
//
//            // Then
//            assertThat(burger.isFavoriteBurger()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción al establecer favorito en hamburguesa que no es de menú")
//        void deberiaLanzarExcepcionAlEstablecerFavoritoEnHamburguesaQueNoEsDeMenu() {
//            // Given
//            Burger hamburguesaPersonalizada = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//
//            // Then
//            assertThatThrownBy(() ->
//                    hamburguesaPersonalizada.setMenuBurgerFavorite(true, 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("Solo burgers de menú");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando updatedBy es null")
//        void deberiaLanzarExcepcionCuandoUpdatedByEsNull() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.setMenuBurgerFavorite(true, null)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("updatedBy no puede ser null");
//        }
//    }
//
//    // ============================================
//    // FUNCIONALIDAD DE FAVORITOS - HAMBURGUESAS PERSONALIZADAS
//    // ============================================
//
//    @Nested
//    @DisplayName("Comportamiento de Favoritos en Hamburguesas Personalizadas")
//    class CustomBurgerFavoriteTests {
//
//        @Test
//        @DisplayName("debería marcar hamburguesa personalizada como favorita")
//        void deberiaMarcarHamburguesaPersonalizadaComoFavorita() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            burger.setIdBurger(1);
//
//            // When
//            burger.markAsFavorite(100);
//
//            // Then
//            assertThat(burger.isFavoriteBurger()).isTrue();
//            assertThat(burger.getUpdatedBy()).isEqualTo(100);
//        }
//
//        @Test
//        @DisplayName("debería desmarcar hamburguesa personalizada como favorita")
//        void deberiaDesmarcarHamburguesaPersonalizadaComoFavorita() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            burger.setIdBurger(1);
//            burger.markAsFavorite(100);
//
//            // When
//            burger.unmarkAsFavorite(100);
//
//            // Then
//            assertThat(burger.isFavoriteBurger()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería alternar estado de favorito en hamburguesa personalizada")
//        void deberiaAlternarEstadoFavoritoEnHamburguesaPersonalizada() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            burger.setIdBurger(1);
//
//            // When
//            burger.toggleCustomFavorite(100);
//
//            // Then
//            assertThat(burger.isFavoriteBurger()).isTrue();
//
//            // When
//            burger.toggleCustomFavorite(100);
//
//            // Then
//            assertThat(burger.isFavoriteBurger()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando no propietario intenta marcar como favorita")
//        void deberiaLanzarExcepcionCuandoNoPropietarioIntentaMarcarComoFavorita() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.markAsFavorite(999)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("Solo el creador");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción al marcar como favorita hamburguesa eliminada")
//        void deberiaLanzarExcepcionAlMarcarComoFavoritaHamburguesaEliminada() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            burger.setIdBurger(1);
//            burger.markCustomAsDeleted(100);
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.markAsFavorite(100)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("eliminada");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción para hamburguesa que no es personalizada")
//        void deberiaLanzarExcepcionParaHamburguesaQueNoEsPersonalizada() {
//            // Given
//            Burger hamburguesaMenu = Burger.createMenuBurger("Menu", BigDecimal.TEN);
//
//            // Then
//            assertThatThrownBy(() ->
//                    hamburguesaMenu.markAsFavorite(1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("Solo hamburguesas personalizadas");
//        }
//    }
//
//    // ============================================
//    // GESTIÓN DE IMÁGENES
//    // ============================================
//
//    @Nested
//    @DisplayName("Comportamiento de Gestión de Imágenes")
//    class ImageManagementTests {
//
//        @Test
//        @DisplayName("debería actualizar imagen exitosamente")
//        void deberiaActualizarImagenExitosamente() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            burger.setIdBurger(1);
//
//            // When
//            burger.updateImageComplete(
//                    "products/1769535390022-b29db968-burger.jpg",
//                    "https://s3.amazonaws.com/bucket/burger.jpg",
//                    1
//            );
//
//            // Then
//            assertThat(burger.getImageKey()).isEqualTo("products/1769535390022-b29db968-burger.jpg");
//            assertThat(burger.getImageUrl()).isEqualTo("https://s3.amazonaws.com/bucket/burger.jpg");
//            assertThat(burger.getImageStatus()).isEqualTo("READY");
//            assertThat(burger.getUpdatedBy()).isEqualTo(1);
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando imageKey está vacío")
//        void deberiaLanzarExcepcionCuandoImageKeyEstaVacio() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateImageComplete("", "url", 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("imageKey no puede estar vacío");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción cuando updatedBy es null")
//        void deberiaLanzarExcepcionCuandoUpdatedByEsNull() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateImageComplete("key", "url", null)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("updatedBy no puede ser null");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción al actualizar imagen de hamburguesa eliminada")
//        void deberiaLanzarExcepcionAlActualizarImagenDeHamburguesaEliminada() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            burger.setIdBurger(1);
//            burger.markAsDeleted(1);
//
//            // Then
//            assertThatThrownBy(() ->
//                    burger.updateImageComplete("key", "url", 1)
//            ).isInstanceOf(InvalidBurgerException.class)
//                    .hasMessageContaining("eliminada");
//        }
//
//        @Test
//        @DisplayName("debería retornar estado NONE cuando no hay imagen")
//        void deberiaRetornarEstadoNoneCuandoNoHayImagen() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//
//            // Then
//            assertThat(burger.getImageStatus()).isEqualTo("NONE");
//        }
//    }
//
//    // ============================================
//    // CÁLCULOS
//    // ============================================
//
//    @Nested
//    @DisplayName("Cálculos de Precios")
//    class PriceCalculationsTests {
//
//        @Test
//        @DisplayName("debería calcular margen correctamente")
//        void deberiaCalcularMargenCorrectamente() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//            burger.updateFinalPrice(new BigDecimal("15.00"), 1);
//
//            // When
//            BigDecimal margen = burger.calculateMargin();
//
//            // Then
//            assertThat(margen).isEqualByComparingTo("5.00");
//        }
//
//        @Test
//        @DisplayName("debería calcular porcentaje de margen correctamente")
//        void deberiaCalcularPorcentajeDeMargenCorrectamente() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//            burger.updateFinalPrice(new BigDecimal("15.00"), 1);
//
//            // When
//            BigDecimal porcentajeMargen = burger.calculateMarginPercentage();
//
//            // Then
//            assertThat(porcentajeMargen).isEqualByComparingTo("50.00");
//        }
//
//        @Test
//        @DisplayName("debería detectar venta con pérdida")
//        void deberiaDetectarVentaConPerdida() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//            burger.updateFinalPrice(new BigDecimal("8.00"), 1);
//
//            // Then
//            assertThat(burger.isSellingAtLoss()).isTrue();
//        }
//
//        @Test
//        @DisplayName("debería detectar venta sin pérdida")
//        void deberiaDetectarVentaSinPerdida() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("10.00"));
//            burger.setIdBurger(1);
//            burger.updateFinalPrice(new BigDecimal("12.00"), 1);
//
//            // Then
//            assertThat(burger.isSellingAtLoss()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería retornar porcentaje de margen cero cuando precio base es cero")
//        void deberiaRetornarPorcentajeDeMargenCeroCuandoPrecioBaseEsCero() {
//            // Given
//            Burger burger = Burger.menuBurger("Hamburguesa", "Desc", null, new ArrayList<>(), false);
//
//            // When
//            BigDecimal porcentajeMargen = burger.calculateMarginPercentage();
//
//            // Then
//            assertThat(porcentajeMargen).isEqualByComparingTo("0.00");
//        }
//    }
//
//    // ============================================
//    // MÉTODOS DE CONSULTA
//    // ============================================
//
//    @Nested
//    @DisplayName("Métodos de Consulta")
//    class QueryMethodsTests {
//
//        @Test
//        @DisplayName("debería retornar true para isAvailable cuando hamburguesa está disponible y no eliminada")
//        void deberiaRetornarTrueParaIsAvailable() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//
//            // Then
//            assertThat(burger.isAvailable()).isTrue();
//        }
//
//        @Test
//        @DisplayName("debería retornar false para isAvailable cuando hamburguesa está eliminada")
//        void deberiaRetornarFalseParaIsAvailableCuandoEstaEliminada() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            burger.setIdBurger(1);
//            burger.markAsDeleted(1);
//
//            // Then
//            assertThat(burger.isAvailable()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería retornar false para isAvailable cuando disponibilidad es false")
//        void deberiaRetornarFalseParaIsAvailableCuandoDisponibilidadEsFalse() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            burger.setIdBurger(1);
//            burger.setAvailability(false, 1);
//
//            // Then
//            assertThat(burger.isAvailable()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería retornar true para canBeOrdered cuando hamburguesa está disponible")
//        void deberiaRetornarTrueParaCanBeOrdered() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//
//            // Then
//            assertThat(burger.canBeOrdered()).isTrue();
//        }
//
//        @Test
//        @DisplayName("debería retornar false para canBeOrdered cuando hamburguesa está eliminada")
//        void deberiaRetornarFalseParaCanBeOrderedCuandoEstaEliminada() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            burger.setIdBurger(1);
//            burger.markAsDeleted(1);
//
//            // Then
//            assertThat(burger.canBeOrdered()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería retornar true para canBeModified cuando no está eliminada")
//        void deberiaRetornarTrueParaCanBeModified() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//
//            // Then
//            assertThat(burger.canBeModified()).isTrue();
//        }
//
//        @Test
//        @DisplayName("debería retornar false para canBeModified cuando está eliminada")
//        void deberiaRetornarFalseParaCanBeModifiedCuandoEstaEliminada() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            burger.setIdBurger(1);
//            burger.markAsDeleted(1);
//
//            // Then
//            assertThat(burger.canBeModified()).isFalse();
//        }
//
//        @Test
//        @DisplayName("debería verificar que hamburguesa pertenece al usuario")
//        void deberiaVerificarQueHamburguesaPerteneceAlUsuario() {
//            // Given
//            Burger burger = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//
//            // Then
//            assertThat(burger.belongsToUser(100)).isTrue();
//            assertThat(burger.belongsToUser(999)).isFalse();
//        }
//    }
//
//    // ============================================
//    // COMPORTAMIENTO GENERAL
//    // ============================================
//
//    @Nested
//    @DisplayName("Comportamiento General")
//    class GeneralBehaviorTests {
//
//        @Test
//        @DisplayName("debería incrementar contador de pedidos")
//        void deberiaIncrementarContadorDePedidos() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", BigDecimal.TEN);
//            assertThat(burger.getTimesOrdered()).isEqualTo(0);
//
//            // When
//            burger.incrementOrders();
//            burger.incrementOrders();
//
//            // Then
//            assertThat(burger.getTimesOrdered()).isEqualTo(2);
//        }
//
//        @Test
//        @DisplayName("debería agregar ingrediente a hamburguesa de menú")
//        void deberiaAgregarIngredienteAHamburguesaDeMenu() {
//            // Given
//            Burger burger = Burger.createMenuBurger("Hamburguesa", new BigDecimal("5.00"));
//            BurgerIngredient ingrediente = crearBurgerIngredient(1, "Queso", new BigDecimal("2.00"), 1);
//
//            // When
//            burger.addIngredient(ingrediente);
//
//            // Then
//            assertThat(burger.getIngredients()).hasSize(1);
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("7.00");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("7.00");
//        }
//
//        @Test
//        @DisplayName("debería lanzar excepción al agregar ingrediente a hamburguesa que no es de menú")
//        void deberiaLanzarExcepcionAlAgregarIngredienteAHamburguesaQueNoEsDeMenu() {
//            // Given
//            Burger hamburguesaPersonalizada = new Burger.CustomBuilder("Personalizada", 100)
//                    .addIngredient(crearProductSnapshot(1, "Pan", BigDecimal.ONE, 1))
//                    .build();
//            BurgerIngredient ingrediente = crearBurgerIngredient(2, "Queso", BigDecimal.ONE, 1);
//
//            // Then
//            assertThatThrownBy(() ->
//                    hamburguesaPersonalizada.addIngredient(ingrediente)
//            ).isInstanceOf(IllegalStateException.class)
//                    .hasMessageContaining("Solo hamburguesas del menú");
//        }
//    }
//
//    // ============================================
//    // RECONSTITUCIÓN
//    // ============================================
//
//    @Nested
//    @DisplayName("Factory Reconstitute")
//    class ReconstituteTests {
//
//        @Test
//        @DisplayName("debería reconstituir hamburguesa desde persistencia")
//        void deberiaReconstiturHamburguesaDesdePersistencia() {
//            // Given
//            LocalDateTime ahora = LocalDateTime.now();
//            List<BurgerIngredient> ingredientes = List.of(
//                    crearBurgerIngredient(1, "Pan", new BigDecimal("2.00"), 1)
//            );
//
//            // When
//            Burger burger = Burger.reconstitute(
//                    1,
//                    "Hamburguesa de Prueba",
//                    "Descripción",
//                    new BigDecimal("10.00"),
//                    new BigDecimal("12.00"),
//                    true,
//                    true,
//                    false,
//                    true,
//                    "products/image.jpg",
//                    "https://s3.com/image.jpg",
//                    null,
//                    5,
//                    ahora,
//                    ahora,
//                    null,
//                    1,
//                    1,
//                    null,
//                    ingredientes
//            );
//
//            // Then
//            assertThat(burger.getIdBurger()).isEqualTo(1);
//            assertThat(burger.getName()).isEqualTo("Hamburguesa de Prueba");
//            assertThat(burger.getDescription()).isEqualTo("Descripción");
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("10.00");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("12.00");
//            assertThat(burger.isMenuBurger()).isTrue();
//            assertThat(burger.isFavoriteBurger()).isTrue();
//            assertThat(burger.isCustomBurger()).isFalse();
//            assertThat(burger.getTimesOrdered()).isEqualTo(5);
//            assertThat(burger.getIngredients()).hasSize(1);
//        }
//
//        @Test
//        @DisplayName("debería manejar valores null en la reconstitución")
//        void deberiaManejarValoresNullEnLaReconstitucion() {
//            // When
//            Burger burger = Burger.reconstitute(
//                    1, "Hamburguesa", null, null, null,
//                    true, false, false, true,
//                    null, null, null, null,
//                    null, null, null, null, null, null, null
//            );
//
//            // Then
//            assertThat(burger.getBasePrice()).isEqualByComparingTo("0.00");
//            assertThat(burger.getFinalPrice()).isEqualByComparingTo("0.00");
//            assertThat(burger.getTimesOrdered()).isEqualTo(0);
//            assertThat(burger.getIngredients()).isEmpty();
//        }
//    }
//
//    // ============================================
//    // MÉTODOS DE AYUDA
//    // ============================================
//
//    private ProductSnapshot crearProductSnapshot(Integer id, String nombre, BigDecimal precio, Integer cantidad) {
//        return new ProductSnapshot() {
//            @Override
//            public Integer getIdProduct() {
//                return id;
//            }
//
//            @Override
//            public String getName() {
//                return nombre;
//            }
//
//            @Override
//            public BigDecimal getUnitPrice() {
//                return precio;
//            }
//
//            @Override
//            public Integer getQuantity() {
//                return cantidad;
//            }
//        };
//    }
//
//    private BurgerIngredient crearBurgerIngredient(Integer id, String nombre, BigDecimal precio, Integer cantidad) {
//        return BurgerIngredient.create(id, nombre, precio, cantidad);
//    }
//}
