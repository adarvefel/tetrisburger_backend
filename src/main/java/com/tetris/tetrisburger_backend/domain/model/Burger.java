package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Burger {

    // ============================================
    // CAMPOS
    // ============================================

    private Integer idBurger;
    private String name;
    private String description;

    // Precios
    private BigDecimal basePrice;
    private BigDecimal finalPrice;

    // Métricas (calculadas y persistidas en DB)
    private BigDecimal margin;
    private BigDecimal marginPercentage;
    private Boolean sellingAtLoss;

    // Discriminadores
    private boolean isOnMenu;
    private boolean isFeatured;   // is_featured en DB (menu) / favorita en DB (custom)
    private boolean isSaved;     // is_saved en DB

    // Estado
    private boolean availability;
    private String imageKey;
    private String imageUrl;
    private Integer idUser;
    private Integer timesOrdered;

    // Ingredientes
    private List<BurgerIngredient> ingredients;

    // Auditoría
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
        this.margin = BigDecimal.ZERO;
        this.marginPercentage = BigDecimal.ZERO;
        this.sellingAtLoss = false;
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
            BigDecimal margin,
            BigDecimal marginPercentage,
            Boolean sellingAtLoss,
            boolean isOnMenu,
            boolean isFeatured,
            boolean isSaved,
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
        b.margin = margin != null ? margin : BigDecimal.ZERO;
        b.marginPercentage = marginPercentage != null ? marginPercentage : BigDecimal.ZERO;
        b.sellingAtLoss = sellingAtLoss != null ? sellingAtLoss : false;
        b.isOnMenu = isOnMenu;
        b.isFeatured = isFeatured;
        b.isSaved = isSaved;
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
    // FACTORY: Burger de MENÚ simple (sin ingredientes)
    // ============================================

    public static Burger createMenuBurger(String name, BigDecimal basePrice) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio base debe ser mayor a 0");

        Burger burger = new Burger();
        burger.name = name;
        burger.basePrice = basePrice;
        burger.finalPrice = basePrice;
        burger.isOnMenu = true;
        burger.isSaved = false;
        burger.isFeatured = false;
        burger.availability = true;
        burger.createdAt = LocalDateTime.now();
        burger.syncMetrics();
        return burger;
    }

    // ============================================
    // FACTORY: Burger de MENÚ con ingredientes
    // ============================================

    public static Burger menuBurger(
            String name,
            String description,
            String imageUrl,
            List<ProductSnapshot> ingredients,
            boolean isFeatured
    ) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        Burger burger = new Burger();
        burger.name = name;
        burger.description = description;
        burger.imageUrl = imageUrl;
        burger.imageKey = null;
        burger.isOnMenu = true;
        burger.isSaved = false;
        burger.isFeatured = isFeatured;
        burger.availability = true;
        burger.idUser = null;
        burger.timesOrdered = 0;
        burger.createdAt = LocalDateTime.now();

        if (ingredients != null) {
            for (ProductSnapshot snapshot : ingredients) {
                if (snapshot == null) continue;
                burger.ingredients.add(BurgerIngredient.fromSnapshot(snapshot));
            }
        }

        burger.basePrice = burger.calculateTotalPriceFromIngredients();
        burger.finalPrice = burger.basePrice;
        burger.syncMetrics();
        return burger;
    }

    // ============================================
    // FACTORY: Builder para CUSTOM
    // ============================================

    public static class CustomBuilder {
        private final Burger burger;

        public CustomBuilder(String name, Integer userId) {
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("El nombre es obligatorio");
            if (userId == null)
                throw new IllegalArgumentException("El ID del usuario es obligatorio");

            this.burger = new Burger();
            burger.name = name;
            burger.isOnMenu = false;
            burger.isSaved = true;
            burger.isFeatured = false;
            burger.availability = true;
            burger.idUser = userId;
            burger.createdAt = LocalDateTime.now();
            burger.createdBy = userId;
        }

        public CustomBuilder addIngredient(ProductSnapshot snapshot) {
            if (snapshot == null)
                throw new IllegalArgumentException("El snapshot no puede ser null");
            burger.ingredients.add(BurgerIngredient.fromSnapshot(snapshot));
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
            if (burger.ingredients.isEmpty())
                throw new IllegalStateException("La hamburguesa debe tener al menos un ingrediente");

            burger.basePrice = burger.calculateTotalPriceFromIngredients();
            burger.finalPrice = burger.basePrice;
            burger.syncMetrics();
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
            Integer updatedBy
    ) {
        if (!this.isOnMenu)
            throw new InvalidBurgerException("Solo hamburguesas del menú pueden actualizarse. ID: " + this.idBurger);
        if (this.isDeleted())
            throw new InvalidBurgerException("No se puede actualizar una hamburguesa eliminada. ID: " + this.idBurger);
        if (name == null || name.isBlank())
            throw new InvalidBurgerException("El nombre no puede estar vacío");
        if (updatedBy == null)
            throw new InvalidBurgerException("updatedBy no puede ser nulo");
        if (newIngredients == null || newIngredients.isEmpty())
            throw new InvalidBurgerException("Debe tener al menos un ingrediente");

        this.name = name;
        this.description = description;
        setIngredients(newIngredients);

        BigDecimal newBasePrice = calculateTotalPriceFromIngredients();

        if (this.finalPrice.compareTo(this.basePrice) == 0) {
            this.basePrice = newBasePrice;
            this.finalPrice = newBasePrice;
        } else {
            this.basePrice = newBasePrice;
            // finalPrice se mantiene si era precio custom
        }

        if (availability != null) this.availability = availability;

        this.syncMetrics();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateFinalPrice(BigDecimal newFinalPrice, Integer updatedBy) {
        if (!this.isOnMenu)
            throw new InvalidBurgerException("Solo hamburguesas del menú pueden cambiar de precio");
        if (newFinalPrice == null || newFinalPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidBurgerException("El precio debe ser mayor a 0");
        if (updatedBy == null)
            throw new InvalidBurgerException("updatedBy no puede ser null");

        this.finalPrice = newFinalPrice;
        this.syncMetrics();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateInfo(String name, String description, Integer updatedBy) {
        if (!this.isOnMenu)
            throw new IllegalStateException("Solo hamburguesas del menú pueden actualizarse");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío");

        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void markAsDeleted(Integer deletedBy) {
        if (deletedBy == null)
            throw new InvalidBurgerException("deletedBy no puede ser null");
        if (this.isDeleted())
            throw new InvalidBurgerException("La burger ID: " + this.idBurger + " ya está eliminada");

        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = deletedBy;
        this.availability = false;
    }

    public void setAvailability(boolean available, Integer updatedBy) {
        if (!this.isOnMenu)
            throw new InvalidBurgerException("Solo hamburguesas del menú pueden cambiar disponibilidad. ID: " + this.idBurger);
        if (updatedBy == null)
            throw new InvalidBurgerException("updatedBy no puede ser null");
        if (this.isDeleted())
            throw new InvalidBurgerException("No se puede cambiar disponibilidad de una hamburguesa eliminada. ID: " + this.idBurger);

        this.availability = available;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    // ============================================
    // COMPORTAMIENTO: Custom Burger
    // ============================================

    public void updateCustomBurger(
            Integer idUser,
            String name,
            String description,
            List<BurgerIngredient> newIngredients
    ) {
        if (!this.isSaved)
            throw new InvalidBurgerException("Solo hamburguesas personalizadas pueden actualizarse. ID: " + this.idBurger);
        if (this.isDeleted())
            throw new InvalidBurgerException("No se puede actualizar una hamburguesa eliminada. ID: " + this.idBurger);
        if (!belongsToUser(idUser))
            throw new InvalidBurgerException("La hamburguesa no pertenece a este usuario");
        if (name == null || name.isBlank())
            throw new InvalidBurgerException("El nombre no puede estar vacío");
        if (newIngredients == null || newIngredients.isEmpty())
            throw new InvalidBurgerException("Debe tener al menos un ingrediente");

        this.name = name;
        this.description = description;
        setIngredients(newIngredients);
        this.basePrice = calculateTotalPriceFromIngredients();
        this.finalPrice = this.basePrice;
        this.syncMetrics();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = idUser;
    }

    public void markCustomAsDeleted(Integer idUser) {
        if (!this.isSaved)
            throw new InvalidBurgerException("Solo hamburguesas personalizadas pueden eliminarse. ID: " + this.idBurger);
        if (this.isDeleted())
            throw new InvalidBurgerException("La hamburguesa ya está eliminada. ID: " + this.idBurger);
        if (!belongsToUser(idUser))
            throw new InvalidBurgerException("La hamburguesa no pertenece a este usuario");

        this.deletedAt = LocalDateTime.now();
        this.availability = false;
        this.deletedBy = idUser;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = idUser;
    }

    // ============================================
    // FAVORITOS: Menu Burger (ADMIN)
    // ============================================

    public void setMenuBurgerFeatured(Boolean isFeatured, Integer updatedBy) {
        if (!this.isOnMenu)
            throw new InvalidBurgerException("Solo burgers de menú pueden marcarse como destacadas. ID: " + this.idBurger);
        if (updatedBy == null)
            throw new InvalidBurgerException("updatedBy no puede ser null");
        if (isFeatured == null)
            throw new InvalidBurgerException("isFavorite no puede ser null");

        this.isFeatured = isFeatured;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void toggleMenuFavorite(Integer updatedBy) {
        if (!this.isOnMenu)
            throw new InvalidBurgerException("Solo burgers de menú pueden marcarse como destacadas. ID: " + this.idBurger);
        if (updatedBy == null)
            throw new InvalidBurgerException("updatedBy no puede ser null");

        this.isFeatured = !this.isFeatured;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    // ============================================
    // FAVORITOS: Custom Burger (USUARIO)
    // ============================================

    public void markAsFavorite(Integer idUser) {
        if (!this.isSaved)
            throw new InvalidBurgerException("Solo hamburguesas personalizadas pueden marcarse como favoritas. ID: " + this.idBurger);
        if (this.isDeleted())
            throw new InvalidBurgerException("No se puede marcar como favorita una hamburguesa eliminada. ID: " + this.idBurger);
        if (!belongsToUser(idUser))
            throw new InvalidBurgerException("Solo el creador puede marcar su hamburguesa como favorita");

        this.isFeatured = true;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = idUser;
    }

    public void unmarkAsFavorite(Integer idUser) {
        if (!this.isSaved)
            throw new InvalidBurgerException("Solo hamburguesas personalizadas pueden desmarcarse. ID: " + this.idBurger);
        if (!belongsToUser(idUser))
            throw new InvalidBurgerException("Solo el creador puede desmarcar su hamburguesa");

        this.isFeatured = false;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = idUser;
    }

    public void toggleCustomFavorite(Integer userId) {
        if (!this.isSaved)
            throw new InvalidBurgerException("Solo hamburguesas personalizadas pueden cambiar favorita. ID: " + this.idBurger);
        if (this.isDeleted())
            throw new InvalidBurgerException("No se puede cambiar favorita de una hamburguesa eliminada. ID: " + this.idBurger);
        if (!belongsToUser(userId))
            throw new InvalidBurgerException("Solo el creador puede cambiar el estado de favorita");

        this.isFeatured = !this.isFeatured;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;
    }

    // ============================================
    // IMAGEN
    // ============================================

    public void updateImageComplete(String imageKey, String imageUrl, Integer updatedBy) {
        if (imageKey == null || imageKey.isBlank())
            throw new InvalidBurgerException("El imageKey no puede estar vacío");
        if (updatedBy == null)
            throw new InvalidBurgerException("updatedBy no puede ser null");
        if (this.isDeleted())
            throw new InvalidBurgerException("No se puede actualizar imagen de una hamburguesa eliminada. ID: " + this.idBurger);

        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    // ============================================
    // CÁLCULOS Y MÉTRICAS
    // ============================================

    /**
     * Sincroniza margin, marginPercentage y sellingAtLoss
     * Llamar siempre que cambien basePrice o finalPrice
     */
    public void syncMetrics() {
        this.margin = calculateMargin();
        this.marginPercentage = calculateMarginPercentage();
        this.sellingAtLoss = isSellingAtLoss();
    }

    private BigDecimal calculateTotalPriceFromIngredients() {
        if (ingredients == null || ingredients.isEmpty()) return BigDecimal.ZERO;
        return ingredients.stream()
                .map(BurgerIngredient::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateMargin() {
        if (basePrice == null || finalPrice == null) return BigDecimal.ZERO;
        return finalPrice.subtract(basePrice);
    }

    public BigDecimal calculateMarginPercentage() {
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return finalPrice.subtract(basePrice)
                .divide(basePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean isSellingAtLoss() {
        if (basePrice == null || finalPrice == null) return false;
        return finalPrice.compareTo(basePrice) < 0;
    }

    public void incrementOrders() {
        this.timesOrdered++;
    }

    public boolean belongsToUser(Integer idUser) {
        return this.idUser != null && this.idUser.equals(idUser);
    }

    public String getImageStatus() {
        return (this.imageKey == null || this.imageKey.isBlank()) ? "NONE" : "READY";
    }

    // ============================================
    // QUERIES DE ESTADO
    // ============================================

    public boolean isMenuBurger()   { return this.isOnMenu; }
    public boolean isCustomBurger() { return this.isSaved; }
    public boolean isAvailable()    { return this.availability && this.deletedAt == null; }
    public boolean isDeleted()      { return this.deletedAt != null; }
    public boolean canBeModified()  { return !this.isDeleted(); }
    public boolean canBeOrdered()   { return this.availability && !this.isDeleted(); }

    // ============================================
    // GETTERS
    // ============================================

    public Integer getIdBurger()            { return idBurger; }
    public String getName()                 { return name; }
    public String getDescription()          { return description; }
    public BigDecimal getBasePrice()        { return basePrice; }
    public BigDecimal getFinalPrice()       { return finalPrice; }
    public BigDecimal getMargin()           { return margin; }
    public BigDecimal getMarginPercentage() { return marginPercentage; }
    public Boolean getSellingAtLoss()       { return sellingAtLoss; }
    public boolean isOnMenu()               { return isOnMenu; }
    public boolean isSaved()               { return isSaved; }
    public boolean isFeatured()             { return isFeatured; }
    public boolean isAvailability()         { return availability; }
    public String getImageKey()             { return imageKey; }
    public String getImageUrl()             { return imageUrl; }
    public Integer getIdUser()              { return idUser; }
    public Integer getTimesOrdered()        { return timesOrdered; }
    public List<BurgerIngredient> getIngredients() { return new ArrayList<>(ingredients); }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public LocalDateTime getDeletedAt()     { return deletedAt; }
    public Integer getCreatedBy()           { return createdBy; }
    public Integer getUpdatedBy()           { return updatedBy; }
    public Integer getDeletedBy()           { return deletedBy; }

    // ============================================
    // SETTERS (solo para infraestructura)
    // ============================================

    public void setIdBurger(Integer idBurger)       { this.idBurger = idBurger; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    public void setCreatedBy(Integer createdBy)     { this.createdBy = createdBy; }
    public void setUpdatedBy(Integer updatedBy)     { this.updatedBy = updatedBy; }
    public void setDeletedBy(Integer deletedBy)     { this.deletedBy = deletedBy; }

    void setIngredients(List<BurgerIngredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }
}
