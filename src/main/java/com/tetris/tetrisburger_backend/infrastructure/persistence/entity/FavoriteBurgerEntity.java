package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Entity
@Table(
        name = "favorite_burger",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_user", "id_burger"})
)
@AllArgsConstructor
@Data
@NoArgsConstructor
public class FavoriteBurgerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorite")
    private Integer idFavorite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_burger", nullable = false)
    private BurgerEntity burger;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ==================== GETTERS Y SETTERS ====================

    public Integer getIdFavorite() { return idFavorite; }
    public void setIdFavorite(Integer idFavorite) { this.idFavorite = idFavorite; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public BurgerEntity getBurger() { return burger; }
    public void setBurger(BurgerEntity burger) { this.burger = burger; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
