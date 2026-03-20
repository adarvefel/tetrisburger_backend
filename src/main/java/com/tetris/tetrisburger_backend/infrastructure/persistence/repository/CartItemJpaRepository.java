package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemJpaRepository extends JpaRepository<CartItemEntity, Integer> {
    List<CartItemEntity> findByCartIdCart(Integer idCart);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItemEntity ci WHERE ci.cart.idCart = :idCart")
    Integer deleteByCart_IdCart(@Param("idCart") Integer idCart);

}