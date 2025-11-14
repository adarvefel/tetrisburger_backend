package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.CreatePqrsRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PqrsRestDtoMapper {

    @Mapping(source = "type", target = "type" )
    @Mapping(source = "subject", target = "subject" )
    @Mapping(source = "description", target = "description" )
    CreatePqrsCommand toCreatePqrsCommand (CreatePqrsRequestDTO createPqrsRequestDTO);
}
