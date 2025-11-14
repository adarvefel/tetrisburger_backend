package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.application.usecase.pqrs.CreatePqrsUseCase;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.CreatePqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs.CreatePqrsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.PqrsRestDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/pqrs")
public class PqrsController {

    private  final CreatePqrs createPqrs;
    private final PqrsRestDtoMapper pqrsRestDtoMapper;

    public PqrsController(CreatePqrs createPqrs, PqrsRestDtoMapper pqrsRestDtoMapper) {
        this.createPqrs = createPqrs;
        this.pqrsRestDtoMapper = pqrsRestDtoMapper;
    }

    @PostMapping
    public ResponseEntity<Pqrs> createPqrs(@RequestBody CreatePqrsRequestDTO createPqrsRequestDTO){

        CreatePqrsCommand command = pqrsRestDtoMapper.toCreatePqrsCommand(createPqrsRequestDTO);
        Pqrs pqrsSaved = createPqrs.handle(command);
        return ResponseEntity.ok(pqrsSaved);
    }

}
