package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "addition_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionSettingsEntity {

    @Id
    @Column(name = "id_settings")
    private Integer idSettings;

    @Column(name = "max_additions_per_item")
    private Integer maxAdditionsPerItem;

    @Column(name = "max_total_price", precision = 10, scale = 2)
    private BigDecimal maxTotalPrice;

    @Column(name = "additions_enabled", nullable = false)
    private boolean additionsEnabled;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}