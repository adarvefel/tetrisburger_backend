// src/main/java/com/tetris/tetrisburger_backend/domain/model/Burger.java
package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio: Burger (Aggregate Root)
 */
public class Burger {

    // ============================================
    // CAMPOS (alineados con la tabla burger)
    // ============================================

    // 1. Identificación
    private Integer idBurger;

    // 2. Información básica
    private String name;
    private String description;

    // 3. Precios
    private BigDecimal basePrice;
    private BigDecimal finalPrice;

    // 4. Discriminadores de tipo
    private boolean isOnMenu;
    private boolean isFavorite;
    private boolean isCustom;

    // 5. Estado
    private boolean availability;
    private String imageUrl;
    private Integer idUser;        // Cliente dueño (si is_custom = true)
    private Integer timesOrdered;

    // 6. Ingredientes (agregado)
    private List<BurgerIngredient> ingredients;

    // 7. Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;



    // ============================================
    // CONSTRUCTOR PRIVADO
    // ============================================

    private Burger() {
        this.ingredients = new ArrayList<>();
        this.timesOrdered = 0;
        this.basePrice = BigDecimal.ZERO;
        this.finalPrice = BigDecimal.ZERO;
        this.availability = true;
    }

    // ============================================
    // FACTORY: Reconstitución desde persistencia
    // ============================================

    public static Burger reconstitute(
            Integer idBurger,
            String name,
            String description,
            BigDecimal basePrice,
            BigDecimal finalPrice,
            boolean isOnMenu,
            boolean isFavorite,
            boolean isCustom,
            boolean availability,
            String imageUrl,
            Integer idUser,
            Integer timesOrdered,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt,
            List<BurgerIngredient> ingredients
    ) {
        Burger b = new Burger();
        b.idBurger = idBurger;
        b.name = name;
        b.description = description;
        b.basePrice = basePrice != null ? basePrice : BigDecimal.ZERO;
        b.finalPrice = finalPrice != null ? finalPrice : BigDecimal.ZERO;  // ← ASIGNAR AQUÍ
        b.isOnMenu = isOnMenu;
        b.isFavorite = isFavorite;
        b.isCustom = isCustom;
        b.availability = availability;
        b.imageUrl = imageUrl;
        b.idUser = idUser;
        b.timesOrdered = timesOrdered != null ? timesOrdered : 0;
        b.createdAt = createdAt;
        b.updatedAt = updatedAt;
        b.deletedAt = deletedAt;
        b.setIngredients(ingredients);

        return b;
    }


    // ============================================
    // FACTORY METHOD: Hamburguesa del MENÚ
    // ============================================

    public static Burger createMenuBurger(String name, BigDecimal basePrice) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio base debe ser mayor a 0");
        }

        Burger burger = new Burger();
        burger.name = name;
        burger.description = null;
        burger.basePrice = basePrice;
        burger.finalPrice = basePrice;
        burger.isOnMenu = true;
        burger.isCustom = false;
        burger.isFavorite = false;
        burger.availability = true;
        burger.createdAt = LocalDateTime.now();
        burger.idUser = null;

        return burger;
    }

    // ============================================
    // FACTORY METHOD: Builder para CUSTOM
    // ============================================

    public static class CustomBuilder {
        private final Burger burger;

        public CustomBuilder(String name, Integer userId) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
            }
            if (userId == null) {
                throw new IllegalArgumentException("El ID del usuario es obligatorio");
            }

            this.burger = new Burger();
            burger.name = name;
            burger.isOnMenu = false;
            burger.isCustom = true;
            burger.isFavorite = false;
            burger.availability = true;
            burger.idUser = userId;
            burger.createdAt = LocalDateTime.now();
            burger.basePrice = BigDecimal.ZERO;


            burger.finalPrice = burger.basePrice;
        }

        public CustomBuilder addIngredient(ProductSnapshot snapshot) {
            if (snapshot == null) {
                throw new IllegalArgumentException("El snapshot no puede ser null");
            }

            BurgerIngredient ingredient = BurgerIngredient.fromSnapshot(snapshot);

            burger.ingredients.add(ingredient);
            burger.finalPrice = burger.finalPrice.add(ingredient.calculateSubtotal());

            return this;
        }

        public CustomBuilder withDescription(String description) {
            burger.description = description;
            return this;
        }

        public CustomBuilder withImage(String imageUrl) {
            burger.imageUrl = imageUrl;
            return this;
        }

        public Burger build() {
            if (burger.ingredients.isEmpty()) {
                throw new IllegalStateException(
                        "La hamburguesa debe tener al menos un ingrediente"
                );
            }
            return burger;
        }
    }

    // FACTORY METHOD: Hamburguesa de MENÚ usando snapshots
    // ============================================

    public static Burger menuBurger(
            String name,
            String description,
            String imageUrl,
            List<ProductSnapshot> ingredients,
            boolean isFavorite
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        Burger burger = new Burger();
        burger.name = name;
        burger.description = description;
        burger.imageUrl = imageUrl;

        burger.isOnMenu = true;      // pertenece al catálogo / menú
        burger.isCustom = false;     // no es de un usuario
        burger.isFavorite = isFavorite;
        burger.availability = true;
        burger.idUser = null;
        burger.timesOrdered = 0;
        burger.createdAt = LocalDateTime.now();

        // Cargar ingredientes desde snapshots
        if (ingredients != null) {
            for (ProductSnapshot snapshot : ingredients) {
                if (snapshot == null) continue;
                BurgerIngredient ingredient = BurgerIngredient.fromSnapshot(snapshot);
                burger.ingredients.add(ingredient);
            }
        }

        ///basePrice = suma de ingredientes (costo, nunca cambia)
        burger.basePrice = burger.calculateTotalPriceInternal(
                burger.ingredients,
                BigDecimal.ZERO
        );

        // finalPrice = igual al basePrice al crear
        burger.finalPrice = burger.basePrice;

        return burger;
    }

    public void updateMenuBurger(
            String name,
            String description,
            String imageUrl,
            List<BurgerIngredient> newIngredients,
            Boolean availability,
            Boolean onMenu,
            Boolean favorite
    ) {
        if (!this.isOnMenu) {
            throw new IllegalStateException(
                    "Solo hamburguesas del menú pueden actualizarse por este método"
            );
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;

        // Reemplazar ingredientes
        setIngredients(newIngredients);
        // Recalcular precio total a partir de basePrice + ingredientes
        this.basePrice = calculateTotalPriceInternal(this.ingredients, BigDecimal.ZERO);

        // Flags de estado
        if (availability != null) {
            this.availability = availability;
        }
        if (onMenu != null) {
            this.isOnMenu = onMenu;
        }
        if (favorite != null) {
            this.isFavorite = favorite;
        }

        this.updatedAt = LocalDateTime.now();
    }



    // ============================================
    // COMPORTAMIENTO DE DOMINIO
    // ============================================

    public void addIngredient(BurgerIngredient ingredient) {
        if (!this.isOnMenu) {
            throw new IllegalStateException(
                    "Solo hamburguesas del menú pueden usar este método directamente"
            );
        }

        if (ingredient == null) {
            throw new IllegalArgumentException("El ingrediente no puede ser null");
        }

        this.ingredients.add(ingredient);

        this.basePrice = this.basePrice.add(ingredient.calculateSubtotal());
        this.finalPrice = this.basePrice;
    }

    public void markAsFavorite(Integer userId) {
        if (!this.isCustom) {
            throw new IllegalStateException(
                    "Solo las hamburguesas personalizadas pueden marcarse como favoritas"
            );
        }

        if (!this.idUser.equals(userId)) {
            throw new IllegalArgumentException(
                    "Solo el creador puede marcar su hamburguesa como favorita"
            );
        }

        this.isFavorite = true;
    }



    public void unmarkAsFavorite(Integer userId) {
        if (!this.idUser.equals(userId)) {
            throw new IllegalArgumentException(
                    "Solo el creador puede desmarcar su favorita"
            );
        }

        this.isFavorite = false;
    }

    public void updatePrice(BigDecimal newPrice) {
        if (!this.isOnMenu) {
            throw new IllegalStateException(
                    "Solo hamburguesas del menú pueden cambiar de precio"
            );
        }

        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor a 0"
            );
        }

        this.basePrice = newPrice;
        this.finalPrice = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateInfo(String name, String description) {
        if (!this.isOnMenu) {
            throw new IllegalStateException(
                    "Solo hamburguesas del menú pueden actualizarse"
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted() {
        if (!this.isOnMenu) {
            throw new IllegalStateException(
                    "Solo hamburguesas del menú pueden eliminarse (soft delete)"
            );
        }

        this.deletedAt = LocalDateTime.now();
        this.availability = false;
    }

    public void setAvailability(boolean available) {
        if (!this.isOnMenu) {
            throw new IllegalStateException(
                    "Solo hamburguesas del menú pueden cambiar disponibilidad"
            );
        }

        this.availability = available;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementOrders() {
        this.timesOrdered++;
    }

    public boolean belongsToUser(Integer userId) {
        return this.idUser != null && this.idUser.equals(userId);
    }

    public BigDecimal calculateTotalPrice() {
        return calculateTotalPriceInternal(this.ingredients, this.basePrice);
    }

    private BigDecimal calculateTotalPriceInternal(List<BurgerIngredient> ingredients, BigDecimal base) {
        BigDecimal ingTotal = ingredients == null
                ? BigDecimal.ZERO
                : ingredients.stream()
                .map(BurgerIngredient::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return base.add(ingTotal);
    }



    // Reglas de CustomBurger

    public void updateCustomBurger(
            Integer idUser,
            String name,
            String description,
            String imageUrl,
            List<BurgerIngredient> newIngredients
    ) {
        if (!this.isCustom) {
            throw new IllegalStateException("Solo hamburguesas personalizadas pueden actualizarse");
        }
        if (!belongsToUser(idUser)) {
            throw new IllegalArgumentException("La hamburguesa no pertenece a este usuario");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        setIngredients(newIngredients);  // método ya existente en el agregado
        this.finalPrice = calculateTotalPriceInternal(this.ingredients, this.basePrice);
        this.updatedAt = LocalDateTime.now();
    }

    public void markCustomAsDeleted(Integer idUser) {
        if (!this.isCustom) {
            throw new IllegalStateException(
                    "Solo hamburguesas personalizadas pueden eliminarse"
            );
        }
        if (!belongsToUser(idUser)) {
            throw new IllegalArgumentException(
                    "La hamburguesa no pertenece a este usuario"
            );
        }

        this.deletedAt = LocalDateTime.now();
        this.availability = false;
    }




    // ============================================
    // QUERIES DE TIPO
    // ============================================

    public boolean isMenuBurger() {
        return this.isOnMenu;
    }

    public boolean isCustomBurger() {
        return this.isCustom;
    }

    public boolean isFavoriteBurger() {
        return this.isFavorite;
    }

    public boolean isAvailable() {
        return this.availability && this.deletedAt == null;
    }

    // ============================================
    // GETTERS
    // ============================================

    public Integer getIdBurger() {
        return idBurger;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public boolean isOnMenu() {
        return isOnMenu;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public boolean isAvailability() {
        return availability;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public Integer getTimesOrdered() {
        return timesOrdered;
    }

    public List<BurgerIngredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }

    // ============================================
    // SOLO PARA RECONSTITUCIÓN / INFRA
    // ============================================

    public void setIdBurger(Integer idBurger) {
        this.idBurger = idBurger;
    }

    void setIngredients(List<BurgerIngredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }
}
