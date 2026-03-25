package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.model.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    long countByOrderDate(LocalDate date);
    long maxDailySequence(LocalDate date);


    Optional<Order> findById(Integer idOrder);
    PageResponse<Order> findByUserId(Integer idUser, PaginationRequest pagination);
    PageResponse<Order> findAll(OrderStatus status, LocalDateTime start,
                                LocalDateTime end, PaginationRequest pagination);
}