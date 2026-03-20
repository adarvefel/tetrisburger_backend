package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper para convertir entre Entity (BD) y Domain (Lógica)
 * Usa MapStruct para generar automáticamente el código
 */
@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    /**
     * Convierte Entity (de la BD) → Domain (para la lógica)
     */
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "userImage", target = "userImage")
    @Mapping(source = "role", target = "role", qualifiedByName = "stringToRole")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy",target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")

    User toDomain(UserEntity entity);

    /**
     * Convierte Domain (lógica) → Entity (para la BD)
     */
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "userImage", target = "userImage")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy",target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")

    UserEntity toEntity(User user);


    /**
     * String → Role (cuando leemos de BD)
     */
    @Named("stringToRole")
    default Role stringToRole(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            return Role.CLIENT;
        }
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.CLIENT;  // Fallback
        }
    }

    /**
     * Role → String (cuando guardamos en BD)
     */
    @Named("roleToString")
    default String roleToString(Role role) {
        if (role == null) return null;
        return role.name();
    }
}
