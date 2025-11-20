package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.application.usecase.pqrs.CreatePqrsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.pqrs.DeleteSoftPqrsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.pqrs.GetPqrsByIdUseCase;
import com.tetris.tetrisburger_backend.application.usecase.pqrs.ListPqrsUseCase;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.*;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.DeleteSoftPqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.UpdatePqrsByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.UpdatePqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.GetPqrsByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.PqrsRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/pqrs")
public class PqrsController {

    private final Logger logger = LoggerFactory.getLogger(PqrsController.class);


    private final PqrsRestDtoMapper pqrsRestDtoMapper;
    private  final CreatePqrs createPqrs;
    private final GetPqrsById getPqrsById;
    private final ListPqrs listPqrs;
    private final DeleteSoftPqrs deleteSoftPqrs;
    private final UpdatePqrs updatePqrs;
    private final UpdatePqrsByAdmin updatePqrsByAdmin;
    private final ListPqrsById listPqrsById;

    public PqrsController(PqrsRestDtoMapper pqrsRestDtoMapper, CreatePqrs createPqrs, GetPqrsById getPqrsById, ListPqrs listPqrs, DeleteSoftPqrs deleteSoftPqrs, UpdatePqrs updatePqrs, UpdatePqrsByAdmin updatePqrsByAdmin, ListPqrsById listPqrsById) {
        this.pqrsRestDtoMapper = pqrsRestDtoMapper;
        this.createPqrs = createPqrs;
        this.getPqrsById = getPqrsById;
        this.listPqrs = listPqrs;
        this.deleteSoftPqrs = deleteSoftPqrs;
        this.updatePqrs = updatePqrs;
        this.updatePqrsByAdmin = updatePqrsByAdmin;
        this.listPqrsById = listPqrsById;
    }

//CRear PQRS

    @PostMapping
    public ResponseEntity<PqrsResponseDTO> createPqrs(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid CreatePqrsRequestDTO createPqrsRequestDTO){

        logger.info("Usuario creando PQRS motivo: {} ", createPqrsRequestDTO.subject());

        Integer idUser = customUserDetails.getId();

        CreatePqrsCommand command = pqrsRestDtoMapper.toCreatePqrsCommand(createPqrsRequestDTO, idUser);
        Pqrs pqrsSaved = createPqrs.handle(command);
        PqrsResponseDTO response = pqrsRestDtoMapper.toPqrsResponseDTO(pqrsSaved);

        logger.info("PQRS creada motivo: {} ", pqrsSaved.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Buscar pqrs por id

    @GetMapping("/{id}")
    public ResponseEntity<PqrsResponseDTO> getPqrsById(@PathVariable Integer id){
        logger.info("Admin buscando PQRS con el ID: {}", id);

        Pqrs pqrs = getPqrsById.handle(new GetPqrsByIdQuery(id));
        PqrsResponseDTO response = pqrsRestDtoMapper.toPqrsResponseDTO(pqrs);

        logger.info("PQRS encontrada con el ID: {}", id);
        return ResponseEntity.ok(response);
    }

    //Listar todos lo pqrs

    @GetMapping
    public ResponseEntity<ListPqrsResponseDTO> listPqrs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idPqrs") String sortBy){


        logger.info("Admin listando pqrs page: {}, size: {}, sortBy: {}", page, size, sortBy);

        ListPqrsQuery listPqrsQuery = new ListPqrsQuery(page, size, sortBy);
        PageResponse<Pqrs> list = listPqrs.handle(listPqrsQuery);
        ListPqrsResponseDTO response = pqrsRestDtoMapper.toListPqrsResponseDTO(list);

        logger.info("Retornando PQRS {} de {} totales", list.content().size(), response.totalElements());

        return ResponseEntity.ok(response);

    }

    //Soft delete

    @DeleteMapping("/{idPqrs}")
    public  ResponseEntity<DeleteSoftPqrsResponseDTO>  softDelete (@PathVariable Integer idPqrs, @AuthenticationPrincipal CustomUserDetails customUserDetails){

        Integer idUser = customUserDetails.getId();

        logger.info("Usuario con ID: {} intenrando eliminar Pqrs con ID: {}", idUser, idPqrs);

        DeleteSoftPqrsCommand deleteSoftPqrsCommand = new DeleteSoftPqrsCommand(
                idPqrs,
                idUser
        );

        deleteSoftPqrs.handle(deleteSoftPqrsCommand);

        logger.info("Usuario con ID: {} eliminno Pqrs con ID: {}", idUser, idPqrs);

        DeleteSoftPqrsResponseDTO response = new DeleteSoftPqrsResponseDTO(
                "PQRS eliminada corretamente.",
                idPqrs
        );

        return ResponseEntity.ok(response);

    }

    //Actualizar pqrs propio,

    @PatchMapping("/{idPqrs}")
    public ResponseEntity<PqrsResponseDTO> updatePqrs(@PathVariable Integer idPqrs,
                                                      @RequestBody UpdatePqrsRequestDTO updatePqrsRequestDTO,
                                                      @AuthenticationPrincipal CustomUserDetails customUserDetails){




        Integer idUser = customUserDetails.getId();

        logger.info("Usuario con id: {} intentando actualizar su PQRS de id: {} ", idUser, idPqrs);

        UpdatePqrsCommand command = pqrsRestDtoMapper.toUpdatePqrsCommand(idPqrs, updatePqrsRequestDTO, idUser);

        Pqrs pqrs = updatePqrs.handle(command);

        PqrsResponseDTO response = pqrsRestDtoMapper.toPqrsResponseDTO(pqrs);

        logger.info("Usuario con id: {} actualizo su PQRS de id: {} ", idUser, idPqrs);

        return ResponseEntity.ok(response);

    }

    //Actualizar pqrs admin

    @PatchMapping("/admin/{idPqrs}")
    public ResponseEntity<PqrsResponseDTO> updatePqrsAdmin(@PathVariable Integer idPqrs,
                                                           @RequestBody UpdatePqrsByAdminRequestDTO updatePqrsByAdminRequestDTO,
                                                           @AuthenticationPrincipal CustomUserDetails customUserDetails){

        Integer assignedTo = customUserDetails.getId();

        logger.info("Staff ID: {} , respondiendo PQRS ID: {}", assignedTo, idPqrs);

        UpdatePqrsByAdminCommand updatePqrsByAdminCommand = pqrsRestDtoMapper.toUpdatePqrsByAdminCommand(idPqrs,
                updatePqrsByAdminRequestDTO,
                assignedTo);

        Pqrs pqrs = updatePqrsByAdmin.handle(updatePqrsByAdminCommand);

        PqrsResponseDTO response = pqrsRestDtoMapper.toPqrsResponseDTO(pqrs);

        logger.info("Staff ID: {} , respondio con exito el PQRS ID: {}", assignedTo, idPqrs);

        return ResponseEntity.ok(response);
    }

    //Obtener lista de los pqrs propios

    @GetMapping("/me")
    public ResponseEntity<ListPqrsResponseDTO> listById(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idPqrs") String sortBy){


        Integer idUser = customUserDetails.getId();

        logger.info("Usuario ID: {} listando sus propias pqrs", idUser);

        ListPqrsQuery listPqrsQuery = new ListPqrsQuery(
                page,
                size,
                sortBy
        );

        PageResponse<Pqrs> pqrs = listPqrsById.handle(listPqrsQuery, idUser);

        ListPqrsResponseDTO response = pqrsRestDtoMapper.toListPqrsResponseDTO(pqrs);


        logger.info("Usuario ID: {} realizo consulta de sus propias pqrs", idUser);

        return ResponseEntity.ok(response);

    }


}
