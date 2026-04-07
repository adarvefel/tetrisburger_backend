package com.tetris.tetrisburger_backend.infrastructure.rest.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Integer idOrder,
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        LocalDateTime orderDate,
        LocalDateTime updatedAt,
        Integer createdBy,
        Integer updatedBy,
        List<OrderItemResponseDTO> items,
        String paymentMethod
) {}