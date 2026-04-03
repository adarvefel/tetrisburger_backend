package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    private Integer idOrder;
    private Integer idUser;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;
    private List<OrderItem> items;
    private PaymentMethod paymentMethod;

    private Order() {
        this.items = new ArrayList<>();
    }

    public static Order create(Integer idUser, List<OrderItem> items) { // ← quitar dailyCount
        if (idUser == null)
            throw new IllegalArgumentException("idUser es obligatorio");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("La orden debe tener al menos un item");

        Order o = new Order();
        o.idUser = idUser;
        o.createdBy = idUser;
        o.status = OrderStatus.PENDING;

        // Formato: ORD-2026-04-02-A3F9
        LocalDate today = LocalDate.now();
        String fecha = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        o.orderNumber = String.format("ORD-%s-%s", fecha, random);

        o.orderDate = LocalDateTime.now();
        o.items = new ArrayList<>(items);
        o.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return o;
    }


    public static Order reconstitute(
            Integer idOrder, Integer idUser, String orderNumber,
            OrderStatus status, BigDecimal totalAmount,
            LocalDateTime orderDate, LocalDateTime updatedAt, LocalDateTime deletedAt,
            Integer createdBy, Integer updatedBy, Integer deletedBy,
            List<OrderItem> items,PaymentMethod paymentMethod
    ) {
        Order o = new Order();
        o.idOrder = idOrder;
        o.idUser = idUser;
        o.orderNumber = orderNumber;
        o.status = status;
        o.totalAmount = totalAmount;
        o.orderDate = orderDate;
        o.updatedAt = updatedAt;
        o.deletedAt = deletedAt;
        o.createdBy = createdBy;
        o.updatedBy = updatedBy;
        o.deletedBy = deletedBy;
        o.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        o.paymentMethod = paymentMethod;
        return o;
    }

    public void cancel(Integer cancelledBy) {
        if (this.status == OrderStatus.CANCELLED_BY_EMPLOYEE)
            throw new IllegalStateException("La orden ya está cancelada");
        this.status = OrderStatus.CANCELLED_BY_EMPLOYEE;
        this.updatedBy = cancelledBy;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(OrderStatus newStatus, Integer updatedBy) {
        this.status = newStatus;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }


    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Integer getIdOrder()             { return idOrder; }
    public Integer getIdUser()              { return idUser; }
    public String getOrderNumber()          { return orderNumber; }
    public OrderStatus getStatus()          { return status; }
    public BigDecimal getTotalAmount()      { return totalAmount; }
    public LocalDateTime getOrderDate()     { return orderDate; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public LocalDateTime getDeletedAt()     { return deletedAt; }
    public Integer getCreatedBy()           { return createdBy; }
    public Integer getUpdatedBy()           { return updatedBy; }
    public Integer getDeletedBy()           { return deletedBy; }
    public List<OrderItem> getItems()       { return new ArrayList<>(items); }
    public void setIdOrder(Integer idOrder) { this.idOrder = idOrder; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
}