package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsQuery;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PqrsEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.PqrsEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.PqrsJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Pqrs> findById(Integer id) {
        logger.info("Buscando PQRS en la base de datos por el ID: {}", id);
        return pqrsJpaRepository.findById(id).map(pqrsEntityMapper::toDomain);
    }

    @Override
    public PageResponse<Pqrs> findAllPqrs(ListPqrsQuery listPqrsQuery) {

        logger.info("Buscando pqrs en la db page: {}, tamaño: {}", listPqrsQuery.page(), listPqrsQuery.size());

        Pageable pageable = PageRequest.of(
                listPqrsQuery.page(),
                listPqrsQuery.size(),
                Sort.by(listPqrsQuery.storBy()).ascending()
                
        );

        Page<PqrsEntity> page = pqrsJpaRepository.findAllByDeletedAtIsNull(pageable);

        List<Pqrs> pqrs = page.getContent().stream()
                .map(pqrsEntityMapper::toDomain)
                .toList();

        logger.info("PQRS encontradas en la db {} de: {}, pagina: {} de: {}", pqrs.size(), page.getTotalElements(), page.getNumber() + 1, page.getTotalPages());

        return new PageResponse<Pqrs>(
                pqrs,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()

        );
    }
}
