package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.model.CartItem;
import com.tetris.tetrisburger_backend.domain.port.out.CartRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.CartEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.CartItemEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.CartEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.CartItemJpaRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.CartJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CartAdapter implements CartRepository {

    private final CartJpaRepository cartJpa;
    private final CartItemJpaRepository cartItemJpa;
    private final CartEntityMapper mapper;

    public CartAdapter(CartJpaRepository cartJpa,
                       CartItemJpaRepository cartItemJpa,
                       CartEntityMapper mapper) {
        this.cartJpa = cartJpa;
        this.cartItemJpa = cartItemJpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Cart> findByUserId(Integer idUser) {
        return cartJpa.findByIdUser(idUser)
                .map(mapper::toDomain);
    }

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);

        if (entity.getCreatedAt() == null)
            entity.setCreatedAt(LocalDateTime.now());

        entity.setUpdatedAt(LocalDateTime.now());

        return mapper.toDomain(cartJpa.save(entity));
    }

    @Override
    @Transactional
    public void replaceItems(Integer idCart, List<CartItem> items) {
        System.out.println(">>> DELETING items for cart: " + idCart);
        int deleted = cartItemJpa.deleteByCart_IdCart(idCart);
        System.out.println(">>> DELETED: " + deleted + " items");
        cartItemJpa.flush();

        CartEntity cartRef = cartJpa.getReferenceById(idCart);
        List<CartItemEntity> entities = items.stream()
                .map(item -> mapper.toEntity(cartRef, item))
                .toList();

        cartItemJpa.saveAll(entities);
        System.out.println(">>> SAVED: " + entities.size() + " items");
    }



    @Override
    public void saveItems(Integer idCart, List<CartItem> items) {
        CartEntity cartRef = cartJpa.getReferenceById(idCart);

        List<CartItemEntity> entities = items.stream()
                .map(item -> mapper.toEntity(cartRef, item))
                .toList();

        cartItemJpa.saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteItemsByCartId(Integer idCart) {
        cartItemJpa.deleteByCart_IdCart(idCart);
        cartItemJpa.flush();
    }
}