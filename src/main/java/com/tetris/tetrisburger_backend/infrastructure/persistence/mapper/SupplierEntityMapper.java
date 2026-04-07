package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.SupplierEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierEntityMapper {

    // ==================== Entity -> Domain ====================

    default Supplier toDomain(SupplierEntity e) {
        if (e == null) return null;
        return Supplier.reconstitute(  // ← of() → reconstitute()
                e.getId(),
                e.getName(),
                e.getPhone(),
                e.getEmail(),
                e.getAddress(),
                e.getRegistrationDate(),
                e.getUpdatedAt(),
                e.getDeletedAt(),
                e.getCreatedBy(),
                e.getUpdatedBy(),
                e.getDeletedBy()
        );
    }

    // ==================== Domain -> Entity ====================

    default SupplierEntity toEntity(Supplier d) {
        if (d == null) return null;
        SupplierEntity e = new SupplierEntity();
        e.setId(d.getId());
        e.setName(d.getName() == null ? null : d.getName().trim());
        e.setPhone(d.getPhone() == null ? null : d.getPhone().trim());
        e.setEmail(d.getEmail() == null ? null : d.getEmail().trim());
        e.setAddress(d.getAddress() == null ? null : d.getAddress().trim());
        e.setRegistrationDate(d.getRegistrationDate());
        e.setUpdatedAt(d.getUpdatedAt());
        e.setDeletedAt(d.getDeletedAt());
        e.setCreatedBy(d.getCreatedBy());
        e.setUpdatedBy(d.getUpdatedBy());
        e.setDeletedBy(d.getDeletedBy());
        return e;
    }

    // ==================== List helpers ====================

    default List<Supplier> toDomainList(List<SupplierEntity> entities) {
        return entities == null ? List.of() : entities.stream().map(this::toDomain).toList();
    }
}