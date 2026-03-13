package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDateTime;

public class FavoriteBurger {

    private Integer idFavorite;
    private Integer idUser;
    private Integer idBurger;
    private String name;
    private LocalDateTime createdAt;

    private FavoriteBurger() {}

    public static FavoriteBurger create(Integer idUser, Integer idBurger, String name) {
        if (idUser == null)
            throw new IllegalArgumentException("idUser no puede ser null");
        if (idBurger == null)
            throw new IllegalArgumentException("idBurger no puede ser null");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío");

        FavoriteBurger f = new FavoriteBurger();
        f.idUser = idUser;
        f.idBurger = idBurger;
        f.name = name.trim();
        f.createdAt = LocalDateTime.now();
        return f;
    }

    public static FavoriteBurger reconstitute(Integer idFavorite, Integer idUser,
                                              Integer idBurger, String name,
                                              LocalDateTime createdAt) {
        FavoriteBurger f = new FavoriteBurger();
        f.idFavorite = idFavorite;
        f.idUser = idUser;
        f.idBurger = idBurger;
        f.name = name;
        f.createdAt = createdAt;
        return f;
    }

    public Integer getIdFavorite() {
        return idFavorite;
    }

    public void setIdFavorite(Integer idFavorite) {
        this.idFavorite = idFavorite;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Integer getIdBurger() {
        return idBurger;
    }

    public void setIdBurger(Integer idBurger) {
        this.idBurger = idBurger;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
