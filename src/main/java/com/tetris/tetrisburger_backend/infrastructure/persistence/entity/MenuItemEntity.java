package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import com.tetris.tetrisburger_backend.domain.model.ItemType;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_burger")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BurgerEntity burger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}