package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsByIdQuery;
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

import java.time.LocalDateTime;
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
    public PageResponse<Pqrs> findAllPqrs(ListPqrsQuery q) {

        Pageable pageable = PageRequest.of(
                q.page(),
                q.size(),
                Sort.by(q.sortBy()).ascending()
        );



        Page<PqrsEntity> page;

        if (q.type() != null) {
            page = pqrsJpaRepository.findAllByTypeAndDeletedAtIsNull(q.type(), pageable);
        } else if (q.status() != null) {
            page = pqrsJpaRepository.findAllByStatusAndDeletedAtIsNull(q.status(), pageable);
        } else if (q.priority() != null) {
            page = pqrsJpaRepository.findAllByPriorityAndDeletedAtIsNull(q.priority(), pageable);
        } else {
            page = pqrsJpaRepository.findAllByDeletedAtIsNull(pageable);
        }

        List<Pqrs> list = page.getContent().stream()
                .map(pqrsEntityMapper::toDomain)
                .toList();

        return new PageResponse<>(
                list,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public void softDeletePqrs(Integer idPqrs, Integer idUser) {

        logger.info("Usuario con ID: {} intenrando eliminar Pqrs de la db con ID: {}", idUser, idPqrs);

        PqrsEntity pqrs = pqrsJpaRepository.findById(idPqrs)
                .orElseThrow(() -> new RuntimeException("Pqrs no encontrada."));


        pqrs.setDeletedAt(LocalDateTime.now());
        pqrs.setDeletedBy(idUser);


        logger.info("Usuario con ID: {} eliminno Pqrs de la db con ID: {}", idUser, idPqrs);

    }

    @Override
    public Pqrs updatePqrs(Pqrs pqrs) {

        logger.info("Usuario con id: {} intentando actualizar su PQRS en la DB de id: {} ", pqrs.getIdUser(), pqrs.getIdPqrs());

        PqrsEntity pqrsEntity = pqrsEntityMapper.toEntity(pqrs);
        pqrsJpaRepository.save(pqrsEntity);
        Pqrs domain = pqrsEntityMapper.toDomain(pqrsEntity);

        logger.info("Usuario con id: {} actualizo su PQRS en la DB de id: {} ", pqrs.getIdUser(), pqrs.getIdPqrs());

        return domain;
    }

    @Override
    public PageResponse<Pqrs> findAllById(ListPqrsByIdQuery listPqrsByIdQuery, Integer idUser) {

        logger.info("Buscando en la DB pqrs del user id: {}", idUser);

        Pageable pageable = PageRequest.of(
                listPqrsByIdQuery.page(),
                listPqrsByIdQuery.size(),
                Sort.by(listPqrsByIdQuery.storBy()).ascending()
        );

        Page<PqrsEntity> page = pqrsJpaRepository.findAllByIdUserAndDeletedAtIsNull(idUser, pageable);

        List<Pqrs> pqrs = page.getContent()
                .stream()
                .map(pqrsEntityMapper::toDomain)
                .toList();


        logger.info("PQRS del user ID: {} encontradas en la DB", idUser);

        return new PageResponse<Pqrs>(
                pqrs,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
