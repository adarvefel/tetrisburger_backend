package com.tetris.tetrisburger_backend.infrastructure.rest.dto.order;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Integer idOrderItem,
        String itemType,
        String itemName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}