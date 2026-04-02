package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import com.tetris.tetrisburger_backend.domain.enums.OrderItemType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_item")
    private Integer idOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order")
    private com.tetris.tetrisburger_backend.infrastructure.persistence.entity.OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type")
    private OrderItemType itemType;

    @Column(name = "id_burger")
    private Integer idBurger;

    @Column(name = "id_product")
    private Integer idProduct;

    @Column(name = "item_name", length = 150)
    private String itemName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "subtotal")
    private BigDecimal subtotal;


}