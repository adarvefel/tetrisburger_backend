package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private BigDecimal basePrice;   // Costo real de ingredientes (NO cambia con updatePrice)
    private BigDecimal finalPrice;  // Precio de venta (puede ser modificado por admin)

    private boolean isOnMenu;
    private boolean isFavorite;
    private boolean isCustom;

    // 5. Estado
    private boolean availability;
    private String imageKey;              // Ruta S3: products/1769535390022-b29db968-reborm.jpg
    private String imageUrl;              // URL completa o nombre original
    private Integer idUser;               // Cliente dueño (si is_custom = true)
    private Integer timesOrdered;

    // 6. Ingredientes (agregado)
    private List<BurgerIngredient> ingredients;

    // 7. Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Integer createdBy;            // Quién creó
    private Integer updatedBy;            // Quién actualizó
    private Integer deletedBy;            // Quién eliminó

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
            String imageKey,
            String imageUrl,
            Integer idUser,
            Integer timesOrdered,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt,
            Integer createdBy,
            Integer updatedBy,
            Integer deletedBy,
            List<BurgerIngredient> ingredients
    ) {
        Burger b = new Burger();
        b.idBurger = idBurger;
        b.name = name;
        b.description = description;
        b.basePrice = basePrice != null ? basePrice : BigDecimal.ZERO;
        b.finalPrice = finalPrice != null ? finalPrice : BigDecimal.ZERO;
        b.isOnMenu = isOnMenu;
        b.isFavorite = isFavorite;
        b.isCustom = isCustom;
        b.availability = availability;
        b.imageKey = imageKey;
        b.imageUrl = imageUrl;
        b.idUser = idUser;
        b.timesOrdered = timesOrdered != null ? timesOrdered : 0;
        b.createdAt = createdAt;
        b.updatedAt = updatedAt;
        b.deletedAt = deletedAt;
        b.createdBy = createdBy;
        b.updatedBy = updatedBy;
        b.deletedBy = deletedBy;
        b.setIngredients(ingredients);

        return b;
    }

    // ============================================
    // FACTORY METHOD: Hamburguesa del MENÚ (simple)
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
    // FACTORY METHOD: Hamburguesa de MENÚ (con ingredientes)
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
        burger.imageKey = null;

        burger.isOnMenu = true;
        burger.isCustom = false;
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

        // basePrice = suma de ingredientes
        burger.basePrice = burger.calculateTotalPriceFromIngredients();

        // finalPrice = igual al basePrice al crear
        burger.finalPrice = burger.basePrice;

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
            burger.finalPrice = BigDecimal.ZERO;
            burger.createdBy = userId;
        }

        public CustomBuilder addIngredient(ProductSnapshot snapshot) {
            if (snapshot == null) {
                throw new IllegalArgumentException("El snapshot no puede ser null");
            }

            BurgerIngredient ingredient = BurgerIngredient.fromSnapshot(snapshot);
            burger.ingredients.add(ingredient);

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

            // Calcular precio final basado en ingredientes
            burger.basePrice = burger.calculateTotalPriceFromIngredients();
            burger.finalPrice = burger.basePrice;

            return burger;
        }
    }

    // ============================================
    // COMPORTAMIENTO: Menu Burger
    // ============================================

    public void updateMenuBurger(
            String name,
            String description,
            List<BurgerIngredient> newIngredients,
            Boolean availability,
            Boolean isOnMenu,
            Boolean favorite,
            Integer updatedBy
    ) {
        if (!this.isOnMenu) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas del menú pueden actualizarse con este método. Burger ID: " + this.idBurger
            );
        }

        if (this.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede actualizar una hamburguesa eliminada. Burger ID: " + this.idBurger
            );
        }

        if (name == null || name.isBlank()) {
            throw new InvalidBurgerException("El nombre no puede estar vacío");
        }

        if (updatedBy == null) {
            throw new InvalidBurgerException("El ID del usuario que actualiza no puede ser nulo");
        }

        if (newIngredients == null || newIngredients.isEmpty()) {
            throw new InvalidBurgerException("La hamburguesa debe tener al menos un ingrediente");
        }

        this.name = name;
        this.description = description;

        setIngredients(newIngredients);

        // Recalcular basePrice según nuevos ingredientes
        BigDecimal newBasePrice = calculateTotalPriceFromIngredients();

        // Si el finalPrice era igual al basePrice anterior, actualizar ambos
        if (this.finalPrice.compareTo(this.basePrice) == 0) {
            this.basePrice = newBasePrice;
            this.finalPrice = newBasePrice;
        } else {
            // Si había un precio custom, solo actualizar el basePrice
            this.basePrice = newBasePrice;
            // finalPrice se mantiene (precio promocional o premium)
        }

        if (availability != null) {
            this.availability = availability;
        }
        if (isOnMenu != null) {
            this.isOnMenu = isOnMenu;
        }
        if (favorite != null) {
            this.isFavorite = favorite;
        }

        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

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

    /**
     * Actualiza SOLO el precio de venta (finalPrice).
     * El precio base (costo de ingredientes) NO cambia.
     *
     * @param newFinalPrice Nuevo precio de venta
     * @param updatedBy ID del usuario que actualiza
     */
    public void updateFinalPrice(BigDecimal newFinalPrice, Integer updatedBy) {
        if (!this.isOnMenu) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas del menú pueden cambiar de precio"
            );
        }

        if (newFinalPrice == null || newFinalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBurgerException(
                    "El precio debe ser mayor a 0"
            );
        }

        if (updatedBy == null) {
            throw new InvalidBurgerException("updatedBy no puede ser null");
        }

        this.finalPrice = newFinalPrice;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateInfo(String name, String description, Integer updatedBy) {
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
        this.updatedBy = updatedBy;
    }

    public void markAsDeleted(Integer deletedBy) {
        if (deletedBy == null) {
            throw new InvalidBurgerException("El ID del usuario que elimina no puede ser null");
        }

        if (this.isDeleted()) {
            throw new InvalidBurgerException(
                    "La burger con ID " + this.idBurger + " ya está eliminada"
            );
        }

        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = deletedBy;
        this.availability = false;
    }

    public void setAvailability(boolean available, Integer updatedBy) {
        if (!this.isOnMenu) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas del menú pueden cambiar disponibilidad. Burger ID: " + this.idBurger
            );
        }

        if (updatedBy == null) {
            throw new InvalidBurgerException("updatedBy no puede ser null");
        }

        if (this.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede cambiar disponibilidad de una hamburguesa eliminada. Burger ID: " + this.idBurger
            );
        }

        this.availability = available;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    // ============================================
    // : Custom Burger
    // ============================================

    public void updateCustomBurger(
            Integer idUser,
            String name,
            String description,
            List<BurgerIngredient> newIngredients
    ) {
        if (!this.isCustom) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas personalizadas pueden actualizarse. Burger ID: " + this.idBurger
            );
        }

        if (this.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede actualizar una hamburguesa eliminada. Burger ID: " + this.idBurger
            );
        }

        if (!belongsToUser(idUser)) {
            throw new InvalidBurgerException(
                    "La hamburguesa no pertenece a este usuario"
            );
        }

        if (name == null || name.isBlank()) {
            throw new InvalidBurgerException("El nombre no puede estar vacío");
        }

        if (newIngredients == null || newIngredients.isEmpty()) {
            throw new InvalidBurgerException("La hamburguesa debe tener al menos un ingrediente");
        }

        this.name = name;
        this.description = description;
        setIngredients(newIngredients);

        // Recalcular precios basado en nuevos ingredientes
        this.basePrice = calculateTotalPriceFromIngredients();
        this.finalPrice = this.basePrice;  // Custom burgers siempre usan precio calculado

        this.updatedAt = LocalDateTime.now();
        this.updatedBy = idUser;
    }

    public void markCustomAsDeleted(Integer idUser) {
        if (!this.isCustom) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas personalizadas pueden eliminarse. Burger ID: " + this.idBurger
            );
        }

        if (this.isDeleted()) {
            throw new InvalidBurgerException(
                    "La hamburguesa ya está eliminada. Burger ID: " + this.idBurger
            );
        }

        if (!belongsToUser(idUser)) {
            throw new InvalidBurgerException(
                    "La hamburguesa no pertenece a este usuario"
            );
        }

        this.deletedAt = LocalDateTime.now();
        this.availability = false;
        this.deletedBy = idUser;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = idUser;
    }

    // ============================================
    // COMPORTAMIENTO: FAVORITOS - MENU BURGERS
    // ============================================

    /**
     * Establece el estado de favorito para hamburguesa de menú (ADMIN)
     * @param isFavorite true para marcar como destacada, false para desmarcar
     * @param updatedBy ID del admin que realiza el cambio
     */
    public void setMenuFavorite(Boolean isFavorite, Integer updatedBy) {
        if (!this.isOnMenu) {
            throw new InvalidBurgerException(
                    "Solo burgers de menú pueden marcarse como destacadas. Burger ID: " + this.idBurger
            );
        }

        if (updatedBy == null) {
            throw new InvalidBurgerException("updatedBy no puede ser null");
        }

        if (isFavorite == null) {
            throw new InvalidBurgerException("isFavorite no puede ser null");
        }

        this.isFavorite = isFavorite;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Alterna el estado de favorito para hamburguesa de menú (ADMIN)
     * @param updatedBy ID del admin que realiza el cambio
     */
    public void toggleMenuFavorite(Integer updatedBy) {
        if (!this.isOnMenu) {
            throw new InvalidBurgerException(
                    "Solo burgers de menú pueden marcarse como destacadas. Burger ID: " + this.idBurger
            );
        }

        if (updatedBy == null) {
            throw new InvalidBurgerException("updatedBy no puede ser null");
        }

        this.isFavorite = !this.isFavorite;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    // ============================================
    // COMPORTAMIENTO: FAVORITOS - CUSTOM BURGERS
    // ============================================

    /**
     * Marca una hamburguesa personalizada como favorita (USUARIO)
     * @param idUser ID del usuario dueño
     */
    public void markAsFavorite(Integer idUser) {
        if (!this.isCustom) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas personalizadas pueden marcarse como favoritas. Burger ID: " + this.idBurger
            );
        }

        if (this.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede marcar como favorita una hamburguesa eliminada. Burger ID: " + this.idBurger
            );
        }

        if (!belongsToUser(idUser)) {
            throw new InvalidBurgerException(
                    "Solo el creador puede marcar su hamburguesa como favorita"
            );
        }

        this.isFavorite = true;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = idUser;
    }

    /**
     * Desmarca una hamburguesa personalizada como favorita (USUARIO)
     * @param idUser ID del usuario dueño
     */
    public void unmarkAsFavorite(Integer idUser) {
        if (!this.isCustom) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas personalizadas pueden desmarcarse como favoritas. Burger ID: " + this.idBurger
            );
        }

        if (!belongsToUser(idUser)) {
            throw new InvalidBurgerException(
                    "Solo el creador puede desmarcar su hamburguesa como favorita"
            );
        }

        this.isFavorite = false;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = idUser;
    }

    /**
     * Alterna el estado de favorito para hamburguesa personalizada (USUARIO)
     * @param userId ID del usuario dueño
     */
    public void toggleCustomFavorite(Integer userId) {
        if (!this.isCustom) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas personalizadas pueden cambiar estado de favorita. Burger ID: " + this.idBurger
            );
        }

        if (this.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede cambiar estado de favorita en una hamburguesa eliminada. Burger ID: " + this.idBurger
            );
        }

        if (!belongsToUser(userId)) {
            throw new InvalidBurgerException(
                    "Solo el creador puede cambiar el estado de favorita"
            );
        }

        this.isFavorite = !this.isFavorite;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;
    }

    // ============================================
    // COMPORTAMIENTO: Imagen
    // ============================================

    /**
     * Actualiza la imagen de la hamburguesa con ruta S3 y URL
     * @param imageKey Ruta en S3 (ej: products/1769535390022-b29db968-reborm.jpg)
     * @param imageUrl URL completa o nombre original
     * @param updatedBy ID del usuario que actualiza
     */
    public void updateImageComplete(String imageKey, String imageUrl, Integer updatedBy) {
        if (imageKey == null || imageKey.isBlank()) {
            throw new InvalidBurgerException("El imageKey no puede estar vacío");
        }

        if (updatedBy == null) {
            throw new InvalidBurgerException("updatedBy no puede ser null");
        }

        if (this.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede actualizar imagen de una hamburguesa eliminada. Burger ID: " + this.idBurger
            );
        }

        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    // ============================================
    // COMPORTAMIENTO GENERAL Y CÁLCULOS
    // ============================================

    public void incrementOrders() {
        this.timesOrdered++;
    }

    public boolean belongsToUser(Integer idUser) {
        return this.idUser != null && this.idUser.equals(idUser);
    }

    /**
     * Calcula el precio total basado en los ingredientes actuales
     */
    private BigDecimal calculateTotalPriceFromIngredients() {
        if (ingredients == null || ingredients.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return ingredients.stream()
                .map(BurgerIngredient::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el margen de ganancia actual (finalPrice - basePrice)
     */
    public BigDecimal calculateMargin() {
        if (basePrice == null || finalPrice == null) {
            return BigDecimal.ZERO;
        }
        return finalPrice.subtract(basePrice);
    }

    /**
     * Calcula el porcentaje de margen ((finalPrice - basePrice) / basePrice * 100)
     */
    public BigDecimal calculateMarginPercentage() {
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return finalPrice.subtract(basePrice)
                .divide(basePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Verifica si se está vendiendo con pérdida (finalPrice < basePrice)
     */
    public boolean isSellingAtLoss() {
        if (basePrice == null || finalPrice == null) {
            return false;
        }
        return finalPrice.compareTo(basePrice) < 0;
    }

    /**
     * Retorna el estado de la imagen: NONE, READY
     */
    public ImageStatus getImageStatus() {
        if (imageUrl != null && !imageUrl.isBlank()) {
            return ImageStatus.UPLOADED;
        }
        return ImageStatus.NONE;
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

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Verifica si la hamburguesa puede ser modificada
     */
    public boolean canBeModified() {
        return !this.isDeleted();
    }

    /**
     * Verifica si la hamburguesa puede ser pedida
     */
    public boolean canBeOrdered() {
        return this.availability && !this.isDeleted();
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

    public String getImageKey() {
        return imageKey;
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

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
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
