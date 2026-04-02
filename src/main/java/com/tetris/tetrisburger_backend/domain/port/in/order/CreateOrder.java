package com.tetris.tetrisburger_backend.domain.port.in.order;

import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;

import java.util.List;

public interface CreateOrder {
    Order handle(Integer idUser, List<CartItemRequestDTO> cartItems);

}
