package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.application.usecase.pqrs.CreatePqrsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.pqrs.DeleteSoftPqrsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.pqrs.GetPqrsByIdUseCase;
import com.tetris.tetrisburger_backend.application.usecase.pqrs.ListPqrsUseCase;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.CreatePqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.DeleteSoftPqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.GetPqrsById;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.ListPqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.DeleteSoftPqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.GetPqrsByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.CreatePqrsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.DeleteSoftPqrsResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.ListPqrsResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.PqrsResponseDTO;
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

    public PqrsController(PqrsRestDtoMapper pqrsRestDtoMapper, CreatePqrs createPqrs, GetPqrsById getPqrsById, ListPqrs listPqrs, DeleteSoftPqrs deleteSoftPqrs) {
        this.pqrsRestDtoMapper = pqrsRestDtoMapper;
        this.createPqrs = createPqrs;
        this.getPqrsById = getPqrsById;
        this.listPqrs = listPqrs;
        this.deleteSoftPqrs = deleteSoftPqrs;
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
}
