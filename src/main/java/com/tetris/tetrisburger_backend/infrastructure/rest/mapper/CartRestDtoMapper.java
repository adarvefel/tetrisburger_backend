package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.model.CartItem;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartRestDtoMapper {

    // ==================== REQUEST → DOMAIN ====================

    default CartItem toDomain(CartItemRequestDTO dto) {
        if (dto == null) return null;

        return CartItem.fromSync(
                dto.typeProduct(),
                dto.idProduct(),
                dto.name(),
                dto.imageUrl(),
                dto.price(),
                dto.quantity()
        );
    }

    default List<CartItem> toDomainList(List<CartItemRequestDTO> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream()
                .map(this::toDomain)
                .toList();
    }

    // ==================== DOMAIN → RESPONSE ====================

    default CartItemResponseDTO toResponseDTO(CartItem domain) {
        if (domain == null) return null;

        return new CartItemResponseDTO(
                domain.getItemType(),
                domain.getIdItem(),
                domain.getName(),
                domain.getUnitPrice(),
                domain.getImageUrl(),
                domain.getQuantity()
        );
    }

    default List<CartItemResponseDTO> toResponseDTOList(Cart cart) {
        if (cart == null) return List.of();
        return cart.getItems().stream()
                .map(this::toResponseDTO)
                .toList();
    }
}