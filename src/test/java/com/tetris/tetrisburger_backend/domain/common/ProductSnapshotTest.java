//package com.tetris.tetrisburger_backend.domain.common;
//
//import com.tetris.tetrisburger_backend.domain.model.Product;
//import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
//import com.tetris.tetrisburger_backend.domain.model.ProductType;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//
//import static org.assertj.core.api.Assertions.*;
//
//@DisplayName("ProductSnapshot Tests")
//class ProductSnapshotTest {
//
//    @Nested
//    @DisplayName("Factory fromProduct")
//    class FromProductTests {
//
//        @Test
//        @DisplayName("should create snapshot from valid product")
//        void shouldCreateSnapshotFromValidProduct() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Lechuga",
//                    new BigDecimal("0.50"),
//                    "Vegetales"
//            );
//
//            // When
//            ProductSnapshot snapshot = ProductSnapshot.fromProduct(product, 2, true);
//
//            // Then
//            assertThat(snapshot).isNotNull();
//            assertThat(snapshot.idProduct()).isEqualTo(1);
//            assertThat(snapshot.name()).isEqualTo("Lechuga");
//            assertThat(snapshot.price()).isEqualByComparingTo("0.50");
//            assertThat(snapshot.categoryName()).isEqualTo("Vegetales");
//            assertThat(snapshot.quantity()).isEqualTo(2);
//            assertThat(snapshot.isOptional()).isTrue();
//        }
//
//        @Test
//        @DisplayName("should use false by default when isOptional is null")
//        void shouldUseFalseByDefaultWhenIsOptionalIsNull() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Tomate",
//                    new BigDecimal("0.75"),
//                    "Vegetales"
//            );
//
//            // When
//            ProductSnapshot snapshot = ProductSnapshot.fromProduct(product, 1, null);
//
//            // Then
//            assertThat(snapshot.isOptional()).isFalse();
//        }
//
//        @Test
//        @DisplayName("should use default category when product has no category")
//        void shouldUseDefaultCategoryWhenProductHasNoCategory() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Ingrediente",
//                    new BigDecimal("1.00"),
//                    null
//            );
//
//            // When
//            ProductSnapshot snapshot = ProductSnapshot.fromProduct(product, 1, false);
//
//            // Then
//            assertThat(snapshot.categoryName()).isEqualTo("Sin categoría");
//        }
//
//        @Test
//        @DisplayName("should throw exception when product is null")
//        void shouldThrowExceptionWhenProductIsNull() {
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(null, 1, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("El producto no puede ser null");
//        }
//
//        @Test
//        @DisplayName("should throw exception when product is not an ingredient")
//        void shouldThrowExceptionWhenProductIsNotAnIngredient() {
//            // Given
//            Product beverageProduct = createProduct(
//                    1,
//                    "Coca Cola",
//                    new BigDecimal("2.00"),
//                    ProductType.BEVERAGE,
//                    "Bebidas"
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(beverageProduct, 1, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("no es un ingrediente de hamburguesa")
//                    .hasMessageContaining("Coca Cola")
//                    .hasMessageContaining("BEVERAGE");
//        }
//
//        @Test
//        @DisplayName("should throw exception when product is a side dish")
//        void shouldThrowExceptionWhenProductIsASideDish() {
//            // Given
//            Product sideProduct = createProduct(
//                    1,
//                    "Papas Fritas",
//                    new BigDecimal("3.00"),
//                    ProductType.SIDE,
//                    "Acompañamientos"
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(sideProduct, 1, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("no es un ingrediente de hamburguesa")
//                    .hasMessageContaining("Papas Fritas")
//                    .hasMessageContaining("SIDE");
//        }
//
//        @Test
//        @DisplayName("should throw exception when quantity is null")
//        void shouldThrowExceptionWhenQuantityIsNull() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Pan",
//                    new BigDecimal("1.00"),
//                    "Panes"
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(product, null, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("La cantidad debe ser mayor a 0");
//        }
//
//        @Test
//        @DisplayName("should throw exception when quantity is zero")
//        void shouldThrowExceptionWhenQuantityIsZero() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Pan",
//                    new BigDecimal("1.00"),
//                    "Panes"
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(product, 0, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("La cantidad debe ser mayor a 0");
//        }
//
//        @Test
//        @DisplayName("should throw exception when quantity is negative")
//        void shouldThrowExceptionWhenQuantityIsNegative() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Pan",
//                    new BigDecimal("1.00"),
//                    "Panes"
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(product, -5, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("La cantidad debe ser mayor a 0");
//        }
//
//        @Test
//        @DisplayName("should throw exception when price is null")
//        void shouldThrowExceptionWhenPriceIsNull() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Ingrediente",
//                    null,
//                    "Categoría"
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(product, 1, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("no tiene un precio válido")
//                    .hasMessageContaining("Ingrediente");
//        }
//
//        @Test
//        @DisplayName("should throw exception when price is zero")
//        void shouldThrowExceptionWhenPriceIsZero() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Ingrediente",
//                    BigDecimal.ZERO,
//                    "Categoría"
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(product, 1, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("no tiene un precio válido");
//        }
//
//        @Test
//        @DisplayName("should throw exception when price is negative")
//        void shouldThrowExceptionWhenPriceIsNegative() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Ingrediente",
//                    new BigDecimal("-1.00"),
//                    "Categoría"
//            );
//
//            // Then
//            assertThatThrownBy(() ->
//                    ProductSnapshot.fromProduct(product, 1, false)
//            ).isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("no tiene un precio válido");
//        }
//
//        @Test
//        @DisplayName("should create snapshot with large quantity")
//        void shouldCreateSnapshotWithLargeQuantity() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Sal",
//                    new BigDecimal("0.10"),
//                    "Condimentos"
//            );
//
//            // When
//            ProductSnapshot snapshot = ProductSnapshot.fromProduct(product, 100, false);
//
//            // Then
//            assertThat(snapshot).isNotNull();
//            assertThat(snapshot.quantity()).isEqualTo(100);
//            assertThat(snapshot.calculateSubtotal()).isEqualByComparingTo("10.00");
//        }
//
//        @Test
//        @DisplayName("should create snapshot with small decimal price")
//        void shouldCreateSnapshotWithSmallDecimalPrice() {
//            // Given
//            Product product = createIngredientProduct(
//                    1,
//                    "Especias",
//                    new BigDecimal("0.05"),
//                    "Condimentos"
//            );
//
//            // When
//            ProductSnapshot snapshot = ProductSnapshot.fromProduct(product, 2, true);
//
//            // Then
//            assertThat(snapshot).isNotNull();
//            assertThat(snapshot.price()).isEqualByComparingTo("0.05");
//            assertThat(snapshot.calculateSubtotal()).isEqualByComparingTo("0.10");
//        }
//    }
//
//    @Nested
//    @DisplayName("Subtotal Calculation")
//    class CalculateSubtotalTests {
//
//        @Test
//        @DisplayName("should calculate subtotal correctly")
//        void shouldCalculateSubtotalCorrectly() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    1,
//                    "Queso",
//                    new BigDecimal("2.50"),
//                    "Lácteos",
//                    3,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = snapshot.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("7.50");
//        }
//
//        @Test
//        @DisplayName("should calculate subtotal with quantity one")
//        void shouldCalculateSubtotalWithQuantityOne() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    1,
//                    "Pan",
//                    new BigDecimal("1.00"),
//                    "Panes",
//                    1,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = snapshot.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("1.00");
//        }
//
//        @Test
//        @DisplayName("should return zero when price is null")
//        void shouldReturnZeroWhenPriceIsNull() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    1,
//                    "Producto",
//                    null,
//                    "Categoría",
//                    2,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = snapshot.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("0.00");
//        }
//
//        @Test
//        @DisplayName("should return zero when quantity is null")
//        void shouldReturnZeroWhenQuantityIsNull() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    1,
//                    "Producto",
//                    new BigDecimal("5.00"),
//                    "Categoría",
//                    null,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = snapshot.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("0.00");
//        }
//
//        @Test
//        @DisplayName("should calculate subtotal with decimals")
//        void shouldCalculateSubtotalWithDecimals() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    1,
//                    "Salsa",
//                    new BigDecimal("0.75"),
//                    "Salsas",
//                    4,
//                    true
//            );
//
//            // When
//            BigDecimal subtotal = snapshot.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("3.00");
//        }
//
//        @Test
//        @DisplayName("should calculate subtotal with large values")
//        void shouldCalculateSubtotalWithLargeValues() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    1,
//                    "Carne Premium",
//                    new BigDecimal("15.99"),
//                    "Carnes",
//                    5,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = snapshot.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("79.95");
//        }
//
//        @Test
//        @DisplayName("should maintain precision with multiple decimals")
//        void shouldMaintainPrecisionWithMultipleDecimals() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    1,
//                    "Ingrediente",
//                    new BigDecimal("1.234"),
//                    "Categoría",
//                    3,
//                    false
//            );
//
//            // When
//            BigDecimal subtotal = snapshot.calculateSubtotal();
//
//            // Then
//            assertThat(subtotal).isEqualByComparingTo("3.702");
//        }
//    }
//
//    @Nested
//    @DisplayName("Record Behavior")
//    class RecordBehaviorTests {
//
//        @Test
//        @DisplayName("should implement equals correctly")
//        void shouldImplementEqualsCorrectly() {
//            // Given
//            ProductSnapshot snapshot1 = new ProductSnapshot(
//                    1, "Queso", new BigDecimal("2.50"), "Lácteos", 2, false
//            );
//            ProductSnapshot snapshot2 = new ProductSnapshot(
//                    1, "Queso", new BigDecimal("2.50"), "Lácteos", 2, false
//            );
//            ProductSnapshot snapshot3 = new ProductSnapshot(
//                    2, "Tomate", new BigDecimal("1.00"), "Vegetales", 1, true
//            );
//
//            // Then
//            assertThat(snapshot1).isEqualTo(snapshot2);
//            assertThat(snapshot1).isNotEqualTo(snapshot3);
//        }
//
//        @Test
//        @DisplayName("should implement hashCode correctly")
//        void shouldImplementHashCodeCorrectly() {
//            // Given
//            ProductSnapshot snapshot1 = new ProductSnapshot(
//                    1, "Queso", new BigDecimal("2.50"), "Lácteos", 2, false
//            );
//            ProductSnapshot snapshot2 = new ProductSnapshot(
//                    1, "Queso", new BigDecimal("2.50"), "Lácteos", 2, false
//            );
//
//            // Then
//            assertThat(snapshot1.hashCode()).isEqualTo(snapshot2.hashCode());
//        }
//
//        @Test
//        @DisplayName("should implement toString correctly")
//        void shouldImplementToStringCorrectly() {
//            // Given
//            ProductSnapshot snapshot = new ProductSnapshot(
//                    1, "Lechuga", new BigDecimal("0.50"), "Vegetales", 2, true
//            );
//
//            // When
//            String toString = snapshot.toString();
//
//            // Then
//            assertThat(toString).contains("ProductSnapshot");
//            assertThat(toString).contains("Lechuga");
//            assertThat(toString).contains("0.50");
//            assertThat(toString).contains("Vegetales");
//        }
//    }
//
//    @Nested
//    @DisplayName("Real Use Cases")
//    class RealUseCasesTests {
//
//        @Test
//        @DisplayName("should create snapshot for burger with multiple ingredients")
//        void shouldCreateSnapshotForBurgerWithMultipleIngredients() {
//            // Given
//            Product cheese = createIngredientProduct(1, "Queso", new BigDecimal("2.00"), "Lácteos");
//            Product lettuce = createIngredientProduct(2, "Lechuga", new BigDecimal("0.50"), "Vegetales");
//            Product tomato = createIngredientProduct(3, "Tomate", new BigDecimal("0.75"), "Vegetales");
//
//            // When
//            ProductSnapshot cheeseSnapshot = ProductSnapshot.fromProduct(cheese, 2, false);
//            ProductSnapshot lettuceSnapshot = ProductSnapshot.fromProduct(lettuce, 1, false);
//            ProductSnapshot tomatoSnapshot = ProductSnapshot.fromProduct(tomato, 2, true);
//
//            // Then
//            assertThat(cheeseSnapshot.calculateSubtotal()).isEqualByComparingTo("4.00");
//            assertThat(lettuceSnapshot.calculateSubtotal()).isEqualByComparingTo("0.50");
//            assertThat(tomatoSnapshot.calculateSubtotal()).isEqualByComparingTo("1.50");
//            assertThat(tomatoSnapshot.isOptional()).isTrue();
//        }
//
//        @Test
//        @DisplayName("should calculate total cost of burger ingredients")
//        void shouldCalculateTotalCostOfBurgerIngredients() {
//            // Given
//            ProductSnapshot bun = new ProductSnapshot(1, "Pan", new BigDecimal("1.50"), "Panes", 2, false);
//            ProductSnapshot meat = new ProductSnapshot(2, "Carne", new BigDecimal("5.00"), "Carnes", 1, false);
//            ProductSnapshot cheese = new ProductSnapshot(3, "Queso", new BigDecimal("2.00"), "Lácteos", 1, false);
//
//            // When
//            BigDecimal total = bun.calculateSubtotal()
//                    .add(meat.calculateSubtotal())
//                    .add(cheese.calculateSubtotal());
//
//            // Then
//            assertThat(total).isEqualByComparingTo("10.00");
//        }
//    }
//
//    // ============================================
//    // HELPER METHODS
//    // ============================================
//
//    private Product createIngredientProduct(Integer id, String name, BigDecimal price, String categoryName) {
//        return createProduct(id, name, price, ProductType.INGREDIENT, categoryName);
//    }
//
//    private Product createProduct(Integer id, String name, BigDecimal price, ProductType type, String categoryName) {
//        ProductCategory category = null;
//        if (categoryName != null) {
//            category = ProductCategory.reconstitute(1, categoryName, "Descripción", null, null, null);
//        }
//
//        return Product.reconstitute(
//                id,
//                name,
//                "Descripción del producto",
//                price,
//                type,
//                true,
//                category,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//    }
//}
