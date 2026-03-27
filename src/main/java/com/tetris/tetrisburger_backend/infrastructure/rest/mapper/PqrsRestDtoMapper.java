package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.UpdatePqrsByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.UpdatePqrsCommand;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PqrsEntity;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PqrsRestDtoMapper {

    //Mapping para dto de crear pqrs
    default CreatePqrsCommand toCreatePqrsCommand(CreatePqrsRequestDTO createPqrsRequestDTO, Integer idUser){

        return new CreatePqrsCommand(
                createPqrsRequestDTO.type(),
                createPqrsRequestDTO.subject(),
                createPqrsRequestDTO.description(),
                idUser
        );
    }

    //Mapping para pqrs Reponse
     @Mapping(source = "idPqrs", target = "idPqrs" )
     @Mapping(source = "type", target = "type" )
     @Mapping(source = "status", target = "status" )
     @Mapping(source = "priority", target = "priority" )
     @Mapping(source = "subject", target = "subject" )
     @Mapping(source = "description", target = "description" )
     @Mapping(source = "response", target = "response" )
     @Mapping(source = "idUser", target = "idUser" )
     @Mapping(source = "assignedTo", target = "assignedTo" )
     @Mapping(source =  "createdAt", target = "createdAt")
     @Mapping(source =  "updatedAt", target = "updatedAt")
     @Mapping(source =  "createdBy", target = "createdBy")
     @Mapping(source =  "updatedBy", target = "updatedBy")
    PqrsResponseDTO toPqrsResponseDTO(Pqrs pqrs);

    //Mapping para el response de list
    default ListPqrsResponseDTO toListPqrsResponseDTO(PageResponse<Pqrs> pageResponse){

        List<PqrsResponseDTO> pqrs = pageResponse
                .content()
                .stream()
                .map(this::toPqrsResponseDTO)
                .toList();

        return new ListPqrsResponseDTO(
                pqrs,
                pageResponse.page(),
                pageResponse.size(),
                pageResponse.totalElements(),
                pageResponse.totalPages()
        );


    }

    //Mapping para update
    default UpdatePqrsCommand toUpdatePqrsCommand(Integer idPqrs, UpdatePqrsRequestDTO updatePqrsRequestDTO,  Integer idUser){
        return new UpdatePqrsCommand(
          idPqrs,
          updatePqrsRequestDTO.type(),
          updatePqrsRequestDTO.subject(),
          updatePqrsRequestDTO.description(),
          idUser
        );

    }

    //Mapping para update admin
    default UpdatePqrsByAdminCommand toUpdatePqrsByAdminCommand(Integer idPqrs,
                                                                UpdatePqrsByAdminRequestDTO updatePqrsByAdminRequestDTO,
                                                                Integer assignedTo){

        return new UpdatePqrsByAdminCommand(
          idPqrs,
          updatePqrsByAdminRequestDTO.status(),
          updatePqrsByAdminRequestDTO.priority(),
          updatePqrsByAdminRequestDTO.response(),
          assignedTo
        );

    }
}
