package com.tetris.tetrisburger_backend.infrastructure.rest.dto.order;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;

import java.util.List;

public record CreateOrderRequestDTO(
        List<CartItemRequestDTO> items
) {}