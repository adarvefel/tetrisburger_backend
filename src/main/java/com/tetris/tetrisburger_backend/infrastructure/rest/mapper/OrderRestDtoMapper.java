package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.OrderItem;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.order.OrderItemResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.order.OrderResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderRestDtoMapper {

    default OrderResponseDTO toResponseDTO(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(this::toItemDTO)
                .toList();

        return new OrderResponseDTO(
                order.getIdOrder(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getOrderDate(),
                order.getUpdatedAt(),      // ← agregar
                order.getCreatedBy(),      // ← agregar
                order.getUpdatedBy(),      // ← agregar
                items,
                order.getPaymentMethod() != null
                        ? order.getPaymentMethod().getDisplayName()
                        : null
        );
    }

    default OrderItemResponseDTO toItemDTO(OrderItem item) {
        if (item == null) return null;
        return new OrderItemResponseDTO(
                item.getIdOrderItem(),
                item.getIdBurger(),
                item.getItemType().name(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}