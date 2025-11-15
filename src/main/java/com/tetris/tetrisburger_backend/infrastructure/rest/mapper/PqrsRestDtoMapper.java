package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.CreatePqrsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.PqrsResponseDTO;
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

     @Mapping(source = "idPqrs", target = "idPqrs" )
     @Mapping(source = "type", target = "type" )
     @Mapping(source = "status", target = "status" )
     @Mapping(source = "priority", target = "priority" )
     @Mapping(source = "subject", target = "subject" )
     @Mapping(source = "description", target = "description" )
     @Mapping(source = "response", target = "response" )
     @Mapping(source = "idUser", target = "idUser" )
     @Mapping(source = "assignedTo", target = "assignedTo" )
    PqrsResponseDTO toPqrsResponseDTO(Pqrs pqrs);
}
