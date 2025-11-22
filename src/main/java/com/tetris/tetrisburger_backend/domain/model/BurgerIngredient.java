// src/main/java/com/tetris/tetrisburger_backend/domain/model/BurgerIngredient.java
package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object: BurgerIngredient
 * Mapea a la tabla: burger_ingredient
 */
public class BurgerIngredient {

    // ID de la tabla burger_ingredient (solo para persistencia)
    private Integer idBurgerIngredient;

    // Referencia al producto (solo ID, no objeto) - mapea a id_product
    private final Integer idProduct;

    // Snapshot del precio en el momento de agregarlo
    private final BigDecimal priceAtTime;

    // Datos propios del ingrediente - mapean a quantity, is_optional
    private final Integer quantity;
    private final Boolean isOptional;

    // ============================================
    // CONSTRUCTOR PRIVADO
    // ============================================

    private BurgerIngredient(Integer idProduct,
                             BigDecimal priceAtTime,
                             Integer quantity,
                             Boolean isOptional) {
        this.idProduct = idProduct;
        this.priceAtTime = priceAtTime;
        this.quantity = quantity;
        this.isOptional = isOptional;
    }

    // ============================================
    // FACTORY METHODS
    // ============================================

    public static BurgerIngredient create(
            Integer idProduct,
            BigDecimal priceAtTime,
            Integer quantity,
            Boolean isOptional
    ) {
        validateInputs(idProduct, priceAtTime, quantity);
        return new BurgerIngredient(
                idProduct,
                priceAtTime,
                quantity,
                isOptional != null ? isOptional : Boolean.FALSE
        );
    }

    public static BurgerIngredient fromSnapshot(ProductSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("El snapshot no puede ser null");
        }
        return create(
                snapshot.productId(),
                snapshot.price(),
                snapshot.quantity(),
                snapshot.isOptional()
        );
    }

    /**
     * Factory Method: Reconstruir desde BD (usado por mapper JPA)
     */
    public static BurgerIngredient reconstitute(
            Integer idBurgerIngredient,
            Integer idProduct,
            BigDecimal priceAtTime,
            Integer quantity,
            Boolean isOptional
    ) {
        BurgerIngredient ingredient = create(
                idProduct,
                priceAtTime,
                quantity,
                isOptional
        );
        ingredient.idBurgerIngredient = idBurgerIngredient;
        return ingredient;
    }

    // ============================================
    // VALIDACIONES
    // ============================================

    private static void validateInputs(Integer idProduct,
                                       BigDecimal priceAtTime,
                                       Integer quantity) {
        if (idProduct == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }
        if (priceAtTime == null || priceAtTime.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor o igual a 0");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }

    // ============================================
    // COMPORTAMIENTO DE NEGOCIO
    // ============================================

    public BigDecimal calculateSubtotal() {
        return priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }

    // ============================================
    // GETTERS
    // ============================================

    public Integer getIdBurgerIngredient() {
        return idBurgerIngredient;
    }

    public Integer getIdProduct() {
        return idProduct;
    }

    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Boolean getIsOptional() {
        return isOptional;
    }

    // ============================================
    // EQUALS & HASHCODE (igualdad por valor)
    // ============================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BurgerIngredient that = (BurgerIngredient) o;
        return Objects.equals(idProduct, that.idProduct) &&
                Objects.equals(priceAtTime, that.priceAtTime) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(isOptional, that.isOptional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, priceAtTime, quantity, isOptional);
    }

    @Override
    public String toString() {
        return "BurgerIngredient{" +
                "idBurgerIngredient=" + idBurgerIngredient +
                ", idProduct=" + idProduct +
                ", priceAtTime=" + priceAtTime +
                ", quantity=" + quantity +
                ", isOptional=" + isOptional +
                '}';
    }
}
