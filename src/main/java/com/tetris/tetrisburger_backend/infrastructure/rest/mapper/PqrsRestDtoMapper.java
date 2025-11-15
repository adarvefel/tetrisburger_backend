package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.CreatePqrsRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PqrsRestDtoMapper {

    default CreatePqrsCommand toCreatePqrsCommand(CreatePqrsRequestDTO createPqrsRequestDTO, Integer idUser){

        return new CreatePqrsCommand(
                createPqrsRequestDTO.type(),
                createPqrsRequestDTO.subject(),
                createPqrsRequestDTO.description(),
                idUser
        );
    }
}
