package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PqrsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PqrsEntityMapper {

    //PAra pasarlo a dominio.

    @Mapping(source = "idPqrs", target = "idPqrs")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "priority", target = "priority")
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "response", target = "response")
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "assignedTo", target = "assignedTo")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")
    Pqrs toDomain(PqrsEntity pqrsEntity);


    //para psarlo a entity

    @Mapping(source = "idPqrs", target = "idPqrs")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "priority", target = "priority")
    @Mapping(source = "subject", target = "subject")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "response", target = "response")
    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "assignedTo", target = "assignedTo")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")
    PqrsEntity toEntity(Pqrs pqrs);


}
