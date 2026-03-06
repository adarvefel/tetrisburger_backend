package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu_item")
    private Integer idMenuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MenuEntity menu;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;

    @Column(name = "id_burger")
    private Integer idBurger;

    @Column(name = "id_product")
    private Integer idProduct;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}