package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDateTime;

public class User {
    private Integer idUser;
    private String userName;
    private String email;
    private String password;
    private String userImage;      // nombre original
    private String userImageKey;   // key de S3
    private Role role;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    // Constructor privado (fuerza uso de factory methods)
    private User() {}

    // Constructor completo (solo para reconstitución desde BD)
    public User(Integer idUser, String userName, String email, String password,
                String userImage, String userImageKey, Role role, String phone,
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
                Integer createdBy, Integer updatedBy, Integer deletedBy) {
        this.idUser = idUser;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.userImage = userImage;
        this.userImageKey = userImageKey;
        this.role = role;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    // ========================================
    // FACTORY METHODS
    // ========================================

    /**
     * Crea un nuevo usuario por Admin
     */
    public static User createByAdmin(
            String userName,
            String email,
            String hashedPassword,
            Role role,
            String phone,
            String imageKey,
            String imageName,
            Integer createdBy
    ) {
        validateUserName(userName);
        validateEmail(email);
        validateHashedPassword(hashedPassword);

        User user = new User();
        user.userName = userName;
        user.email = email;
        user.password = hashedPassword;
        user.role = role != null ? role : Role.CLIENT;
        user.phone = phone;
        user.userImageKey = imageKey;
        user.userImage = imageName;
        user.createdAt = LocalDateTime.now();
        user.createdBy = createdBy;

        return user;
    }

    /**
     * Crea un nuevo cliente (registro público)
     */
    public static User createClient(
            String userName,
            String email,
            String hashedPassword
    ) {
        validateUserName(userName);
        validateEmail(email);
        validateHashedPassword(hashedPassword);

        User user = new User();
        user.userName = userName;
        user.email = email;
        user.password = hashedPassword;
        user.role = Role.CLIENT;
        user.createdAt = LocalDateTime.now();

        return user;
    }

    // ========================================
    // MÉTODOS DE NEGOCIO
    // ========================================

    /**
     * Actualiza la imagen del usuario
     */
    public void updateImage(String imageKey, String imageName, Integer updatedBy) {
        this.userImageKey = imageKey;
        this.userImage = imageName;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza el perfil básico (nombre y teléfono)
     */
    public void updateProfile(String userName, String phone, Integer updatedBy) {
        if (userName != null && !userName.isBlank()) {
            validateUserName(userName);
            this.userName = userName;
        }
        if (phone != null) {
            this.phone = phone;
        }
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Admin actualiza TODO el perfil del usuario
     */
    public void updateByAdmin(
            String userName,
            String email,
            Role role,
            String phone,
            Integer updatedBy
    ) {
        if (userName != null && !userName.isBlank()) {
            validateUserName(userName);
            this.userName = userName;
        }

        if (email != null && !email.isBlank()) {
            validateEmail(email);
            this.email = email;
        }



        if (role != null) {
            this.role = role;
        }

        if (phone != null) {
            this.phone = phone;
        }

        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Resetea la contraseña (ya debe venir hasheada)
     */
    public void resetPassword(String hashedPassword, Integer updatedBy) {
        validateHashedPassword(hashedPassword);
        this.password = hashedPassword;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Soft delete
     */
    public void markAsDeleted(Integer deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Verifica si está activo
     */
    public boolean isActive() {
        return this.deletedAt == null;
    }

    /**
     * Verifica si el usuario tiene un rol administrativo
     */
    public boolean isAdministrative() {
        return this.role != null && this.role.isAdministrativeRol();
    }

    /**
     * Verifica si puede editar otro usuario
     */
    public boolean canEdit(User otherUser) {
        if (this.role == null || otherUser.role == null) {
            return false;
        }
        return this.role.hasEqualOrHigherHierarchy(otherUser.role);
    }

    // ========================================
    // VALIDACIONES DE DOMINIO
    // ========================================

    private static void validateUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario es requerido");
        }
        if (userName.length() < 3 || userName.length() > 50) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 50 caracteres");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }

    private static void validateHashedPassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña hasheada es requerida");
        }
    }

    // ========================================
    // GETTERS (sin setters públicos)
    // ========================================

    public Integer getIdUser() { return idUser; }
    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUserImage() { return userImage; }
    public String getUserImageKey() { return userImageKey; }
    public Role getRole() { return role; }
    public String getPhone() { return phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public Integer getCreatedBy() { return createdBy; }
    public Integer getUpdatedBy() { return updatedBy; }
    public Integer getDeletedBy() { return deletedBy; }

    // Solo para reconstitución desde BD (JPA/MyBatis necesita esto)
    public void setIdUser(Integer idUser) { this.idUser = idUser; }
}
