package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Integer> {

    @Query(
            value = "SELECT o FROM OrderEntity o LEFT JOIN FETCH o.payment WHERE o.idUser = :idUser AND o.deletedAt IS NULL ORDER BY o.idOrder DESC",
            countQuery = "SELECT COUNT(o) FROM OrderEntity o WHERE o.idUser = :idUser AND o.deletedAt IS NULL"
    )
    Page<OrderEntity> findByIdUser(@Param("idUser") Integer idUser, Pageable pageable);

    @Query(
            value = """
            SELECT o FROM OrderEntity o
            LEFT JOIN FETCH o.payment
            WHERE o.deletedAt IS NULL
              AND (:status IS NULL OR o.status = :status)
              AND (:start IS NULL OR o.orderDate >= :start)
              AND (:end IS NULL OR o.orderDate < :end)
            ORDER BY o.idOrder DESC
        """,
            countQuery = """
            SELECT COUNT(o) FROM OrderEntity o
            WHERE o.deletedAt IS NULL
              AND (:status IS NULL OR o.status = :status)
              AND (:start IS NULL OR o.orderDate >= :start)
              AND (:end IS NULL OR o.orderDate < :end)
        """
    )
    Page<OrderEntity> findAllWithFilters(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    @Query(
            value = """
            SELECT o FROM OrderEntity o
            LEFT JOIN FETCH o.payment
            WHERE o.deletedAt IS NULL
            AND UPPER(o.orderNumber) LIKE UPPER(CONCAT('%', :orderNumber, '%'))
            ORDER BY o.idOrder DESC
        """,
            countQuery = """
            SELECT COUNT(o) FROM OrderEntity o
            WHERE o.deletedAt IS NULL
            AND UPPER(o.orderNumber) LIKE UPPER(CONCAT('%', :orderNumber, '%'))
        """
    )
    Page<OrderEntity> findByOrderNumberContainingIgnoreCase(
            @Param("orderNumber") String orderNumber,
            Pageable pageable
    );
}