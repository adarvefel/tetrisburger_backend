package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.in.order.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.order.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.OrderRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrder createOrder;
    private final GetOrderById getOrderById;
    private final ListUserOrders listUserOrders;
    private final CancelOrder cancelOrder;
    private final ListAllOrders listAllOrders;
    private final UpdateOrderStatus updateOrderStatus;
    private final OrderRestDtoMapper mapper;
    private final SearchOrderByNumber searchOrderByNumber;

    public OrderController(CreateOrder createOrder, GetOrderById getOrderById, ListUserOrders listUserOrders, CancelOrder cancelOrder, ListAllOrders listAllOrders, UpdateOrderStatus updateOrderStatus, OrderRestDtoMapper mapper, SearchOrderByNumber searchOrderByNumber) {
        this.createOrder = createOrder;
        this.getOrderById = getOrderById;
        this.listUserOrders = listUserOrders;
        this.cancelOrder = cancelOrder;
        this.listAllOrders = listAllOrders;
        this.updateOrderStatus = updateOrderStatus;
        this.mapper = mapper;
        this.searchOrderByNumber = searchOrderByNumber;
    }


    // ── Cliente ──────────────────────────────────────

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> create(
            @RequestBody CreateOrderRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Order order = createOrder.handle(user.getId(), dto.items());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDTO(order));
    }

    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<OrderResponseDTO>> myOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        PageResponse<Order> result = listUserOrders.handle(
                user.getId(), new PaginationRequest(page, size));

        PageResponse<OrderResponseDTO> response = new PageResponse<>(
                result.content().stream().map(mapper::toResponseDTO).toList(),
                result.page(), result.size(),
                result.totalElements(), result.totalPages()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> getById(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                mapper.toResponseDTO(getOrderById.handle(id, user.getId())));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> cancel(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(
                mapper.toResponseDTO(cancelOrder.handle(id, user.getId())));
    }

    // ── Employee / Admin ─────────────────────────────

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<PageResponse<Order>> listAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        OrderStatus orderStatus = status != null ? OrderStatus.valueOf(status) : null;

        PageResponse<Order> result = listAllOrders.handle(
                orderStatus, date, new PaginationRequest(page, size));

        return ResponseEntity.ok(result);
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(
                mapper.toResponseDTO(
                        updateOrderStatus.handle(id, newStatus, user.getId())));
    }


    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<PageResponse<OrderResponseDTO>> search(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<Order> result = searchOrderByNumber.handle(
                orderNumber,
                new PaginationRequest(page, size, null, "DESC")
        );
        return ResponseEntity.ok(result.map(mapper::toResponseDTO));
    }
}
