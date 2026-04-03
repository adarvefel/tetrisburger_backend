package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.OrderEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class OrderAdapter implements OrderRepository {

    private final OrderJpaRepository jpa;
    private final OrderEntityMapper mapper;

    public OrderAdapter(OrderJpaRepository jpa, OrderEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        return mapper.toDomain(jpa.save(mapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(Integer idOrder) {
        return jpa.findById(idOrder).map(mapper::toDomain);
    }

    @Override
    public PageResponse<Order> findByUserId(Integer idUser, PaginationRequest pagination) {
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        Page<Order> page = jpa.findByIdUser(idUser, pageable).map(mapper::toDomain);
        return new PageResponse<>(
                page.getContent(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }

    @Override
    public PageResponse<Order> findAll(OrderStatus status, LocalDateTime start,
                                       LocalDateTime end, PaginationRequest pagination) {
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        Page<Order> page = jpa.findAllWithFilters(status, start, end, pageable)
                .map(mapper::toDomain);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public PageResponse<Order> findByOrderNumber(String orderNumber, PaginationRequest pagination) {
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        Page<Order> page = jpa.findByOrderNumberContainingIgnoreCase(orderNumber, pageable)
                .map(mapper::toDomain);
        return new PageResponse<>(
                page.getContent(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages()
        );
    }
}