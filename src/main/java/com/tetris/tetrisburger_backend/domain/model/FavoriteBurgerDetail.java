package com.tetris.tetrisburger_backend.domain.model;


public class FavoriteBurgerDetail {

    private final FavoriteBurger favorite;
    private final Burger burger;

    private FavoriteBurgerDetail(FavoriteBurger favorite, Burger burger) {
        this.favorite = favorite;
        this.burger = burger;
    }

    public static FavoriteBurgerDetail of(FavoriteBurger favorite, Burger burger) {
        if (favorite == null)
            throw new IllegalArgumentException("El favorito no puede ser null");
        return new FavoriteBurgerDetail(favorite, burger);
    }

    public FavoriteBurger getFavorite() { return favorite; }
    public Burger getBurger()           { return burger; }
}
