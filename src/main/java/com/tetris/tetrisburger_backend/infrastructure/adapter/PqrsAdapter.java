package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PqrsEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.PqrsEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.PqrsJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PqrsAdapter implements PqrsPort {

    private  static  final Logger logger = LoggerFactory.getLogger(PqrsAdapter.class);

    private final PqrsJpaRepository pqrsJpaRepository;
    private final PqrsEntityMapper pqrsEntityMapper;

    public PqrsAdapter(PqrsJpaRepository pqrsJpaRepository, PqrsEntityMapper pqrsEntityMapper) {
        this.pqrsJpaRepository = pqrsJpaRepository;
        this.pqrsEntityMapper = pqrsEntityMapper;
    }

    @Override
    public Pqrs savePqrs(Pqrs pqrs) {
        logger.info("Guardando PQRS en base de datos para el user con id: {}", pqrs.getIdUser());
        try {
            PqrsEntity entity = pqrsEntityMapper.toEntity(pqrs);
            PqrsEntity saved = pqrsJpaRepository.save(entity);
            logger.info("PQRS guardada en la base de datos con id: {}", saved.getIdPqrs());
            return pqrsEntityMapper.toDomain(saved);
        }
        catch (Exception e){
            logger.error("Error al guardar en la base de datos el PQRS con id: {}: {}", pqrs.getIdPqrs(), e);
            throw e;
        }
    }
}
