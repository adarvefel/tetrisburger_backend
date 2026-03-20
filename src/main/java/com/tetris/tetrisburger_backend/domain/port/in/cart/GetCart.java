package com.tetris.tetrisburger_backend.domain.port.in.cart;

import com.tetris.tetrisburger_backend.domain.model.Cart;

public interface GetCart {
    Cart handle(Integer idUser);

}
