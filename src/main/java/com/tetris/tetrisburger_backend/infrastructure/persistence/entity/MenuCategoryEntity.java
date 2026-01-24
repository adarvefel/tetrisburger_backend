package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_category")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu_category")
    private Integer idMenuCategory;

    @Column(name = "menu_category_name", nullable = false, length = 100)
    private String menuCategoryName;

    @Column(name = "description", length = 255)
    private String description;

}
