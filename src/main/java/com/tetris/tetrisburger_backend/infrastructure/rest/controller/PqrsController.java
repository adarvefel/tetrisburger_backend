package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.application.usecase.pqrs.CreatePqrsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.pqrs.GetPqrsByIdUseCase;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.CreatePqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.GetPqrsByIdQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.CreatePqrsRequestDTO;
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
    private final GetPqrsByIdUseCase getPqrsByIdUseCase;

    public PqrsController(PqrsRestDtoMapper pqrsRestDtoMapper, CreatePqrs createPqrs, GetPqrsByIdUseCase getPqrsByIdUseCase) {
        this.pqrsRestDtoMapper = pqrsRestDtoMapper;
        this.createPqrs = createPqrs;
        this.getPqrsByIdUseCase = getPqrsByIdUseCase;
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
        logger.info("Usuario intentando buscar PQRS con el ID: {}", id);

        Pqrs pqrs = getPqrsByIdUseCase.handle(new GetPqrsByIdQuery(id));
        PqrsResponseDTO response = pqrsRestDtoMapper.toPqrsResponseDTO(pqrs);

        logger.info("PQRS encontrada con el ID: {}", id);
        return ResponseEntity.ok(response);
    }

}
