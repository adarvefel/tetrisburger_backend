package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerIngredientEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BurgerIngredientJpaRepository extends JpaRepository<BurgerIngredientEntity, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE BurgerIngredientEntity bi SET bi.priceAtTime = :price, bi.productName = :name, bi.imageUrl = :imageUrl, bi.subtotal = bi.quantity * :price WHERE bi.product.id = :idProduct")
    int updateByProductId(
            @Param("idProduct") Integer idProduct,
            @Param("price") BigDecimal price,
            @Param("name") String name,
            @Param("imageUrl") String imageUrl
    );

    @Query("SELECT DISTINCT bi.burger.idBurger FROM BurgerIngredientEntity bi WHERE bi.product.id = :idProduct")
    List<Integer> findBurgerIdsByProductId(@Param("idProduct") Integer idProduct);

    @Modifying
    @Transactional
    @Query("""
    UPDATE BurgerEntity b 
    SET b.basePrice = (
        SELECT SUM(bi.subtotal) FROM BurgerIngredientEntity bi WHERE bi.burger.idBurger = b.idBurger
    ),
    b.finalPrice = (
        SELECT SUM(bi.subtotal) FROM BurgerIngredientEntity bi WHERE bi.burger.idBurger = b.idBurger
    ),
    b.updatedAt = CURRENT_TIMESTAMP
    WHERE b.idBurger IN (
        SELECT DISTINCT bi2.burger.idBurger FROM BurgerIngredientEntity bi2 WHERE bi2.product.id = :idProduct
    )
    AND b.isOnMenu = false
    AND b.deletedAt IS NULL
""")
    void recalculateBasePriceForCustomBurgers(@Param("idProduct") Integer idProduct);
}