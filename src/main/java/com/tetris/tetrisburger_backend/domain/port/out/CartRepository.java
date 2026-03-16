package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartRepository {


    Optional<Cart> findByUserId(Integer idUser);

    Cart save(Cart cart);

    void replaceItems(Integer idCart, List<CartItem> items);


    void deleteItemsByCartId(Integer idCart);

    void saveItems(Integer idCart, List<CartItem> items);
}
