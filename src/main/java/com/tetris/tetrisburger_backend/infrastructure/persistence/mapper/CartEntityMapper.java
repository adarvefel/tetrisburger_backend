package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.model.CartItem;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.CartEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.CartItemEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CartEntityMapper {

    // ── Cart ──────────────────────────────────────────────────────────────────

    public Cart toDomain(CartEntity entity) {
        List<CartItem> items = entity.getItems().stream()
                .map(this::toDomain)
                .toList();

        return Cart.reconstitute(
                entity.getIdCart(),
                entity.getIdUser(),
                items,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public CartEntity toEntity(Cart domain) {
        return CartEntity.builder()
                .idCart(domain.getIdCart())
                .idUser(domain.getIdUser())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    // ── CartItem ──────────────────────────────────────────────────────────────

    public CartItem toDomain(CartItemEntity entity) {
        return CartItem.reconstitute(
                entity.getIdCartItem(),
                entity.getCart().getIdCart(),
                entity.getItemType(),
                entity.getIdItem(),
                entity.getName(),
                entity.getImageUrl(),
                entity.getUnitPrice(),
                entity.getQuantity(),
                entity.getSubtotal(),
                entity.getCreatedAt()
        );
    }

    public CartItemEntity toEntity(CartEntity cart, CartItem domain) {
        return CartItemEntity.builder()
                .cart(cart)
                .itemType(domain.getItemType())
                .idItem(domain.getIdItem())
                .name(domain.getName())
                .imageUrl(domain.getImageUrl())
                .unitPrice(domain.getUnitPrice())
                .quantity(domain.getQuantity())
                .subtotal(domain.getSubtotal())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now())
                .build();
    }
}