package com.tetris.tetrisburger_backend.domain.port.in.cart;

import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.model.CartItem;

import java.util.List;

public interface AddCart {
    Cart handle(Integer idUser, List<CartItem> items);

}
