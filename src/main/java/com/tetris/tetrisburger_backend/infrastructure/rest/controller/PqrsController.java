package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.application.usecase.pqrs.CreatePqrsUseCase;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.CreatePqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.CreatePqrsRequestDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/pqrs")
public class PqrsController {

    private final Logger logger = LoggerFactory.getLogger(PqrsController.class);

    private  final CreatePqrs createPqrs;
    private final PqrsRestDtoMapper pqrsRestDtoMapper;

    public PqrsController(CreatePqrs createPqrs, PqrsRestDtoMapper pqrsRestDtoMapper) {
        this.createPqrs = createPqrs;
        this.pqrsRestDtoMapper = pqrsRestDtoMapper;
    }

    @PostMapping
    public ResponseEntity<Pqrs> createPqrs(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid CreatePqrsRequestDTO createPqrsRequestDTO){

        logger.info("Usuario creando PQRS motivo: {} ", createPqrsRequestDTO.subject());


        Integer idUser = customUserDetails.getId();

        CreatePqrsCommand command = pqrsRestDtoMapper.toCreatePqrsCommand(createPqrsRequestDTO, idUser);

        Pqrs pqrsSaved = createPqrs.handle(command);

        logger.info("PQRS creada motivo: {} ", pqrsSaved.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(pqrsSaved);
    }

}
