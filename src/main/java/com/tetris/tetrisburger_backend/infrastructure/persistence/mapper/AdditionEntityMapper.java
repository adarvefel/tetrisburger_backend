package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.AdditionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdditionEntityMapper {

    // ==================== Entity -> Domain ====================

    default Addition toDomain(AdditionEntity e) {
        if (e == null) return null;

        return Addition.of(
                e.getIdAddition(),
                trim(e.getName()),
                trim(e.getDescription()),
                e.getPrice(),
                e.getAvailable(),
                trim(e.getImageUrl()),
                trim(e.getImageKey()),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt(),
                e.getCreatedBy(),
                e.getUpdatedBy(),
                e.getDeletedBy()
        );
    }

    // ==================== Domain -> Entity ====================

    default AdditionEntity toEntity(Addition d) {
        if (d == null) return null;

        AdditionEntity e = new AdditionEntity();
        e.setIdAddition(d.getIdAddition());
        e.setName(trim(d.getName()));
        e.setDescription(trim(d.getDescription()));
        e.setPrice(d.getPrice());
        e.setAvailable(d.getAvailable());
        e.setImageUrl(trim(d.getImageUrl()));
        e.setImageKey(trim(d.getImageKey()));
        e.setDeletedAt(d.getDeletedAt());
        e.setCreatedBy(d.getCreatedBy());
        e.setUpdatedBy(d.getUpdatedBy());
        e.setDeletedBy(d.getDeletedBy());

        return e;
    }

    // ==================== Utils ====================

    default String trim(String s) {
        return s == null ? null : s.trim();
    }
}