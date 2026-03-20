package com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart;

import com.tetris.tetrisburger_backend.domain.model.CartItem;

import java.math.BigDecimal;

public record CartItemResponseDTO(
        CartItem.ItemType typeProduct,
        Integer idProduct,
        String name,
        BigDecimal price,
        String imageUrl,
        Integer quantity
) {}