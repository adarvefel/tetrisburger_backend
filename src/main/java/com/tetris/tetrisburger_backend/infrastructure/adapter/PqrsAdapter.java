package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsQuery;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PqrsEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.PqrsEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.PqrsJpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PqrsAdapter implements PqrsPort {

    private final PqrsJpaRepository pqrsJpaRepository;
    private final PqrsEntityMapper pqrsEntityMapper;

    public PqrsAdapter(PqrsJpaRepository pqrsJpaRepository, PqrsEntityMapper pqrsEntityMapper) {
        this.pqrsJpaRepository = pqrsJpaRepository;
        this.pqrsEntityMapper = pqrsEntityMapper;
    }

    @Override
    public Pqrs savePqrs(Pqrs pqrs) {
        try {
            PqrsEntity entity = pqrsEntityMapper.toEntity(pqrs);
            PqrsEntity saved = pqrsJpaRepository.save(entity);
            return pqrsEntityMapper.toDomain(saved);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Optional<Pqrs> findById(Integer id) {
        return pqrsJpaRepository.findById(id)
                .map(pqrsEntityMapper::toDomain);
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

        List<Pqrs> list = page.getContent()
                .stream()
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

        PqrsEntity pqrs = pqrsJpaRepository.findById(idPqrs)
                .orElseThrow(() -> new RuntimeException("Pqrs no encontrada."));

        pqrs.setDeletedAt(LocalDateTime.now());
        pqrs.setDeletedBy(idUser);
    }

    @Override
    public Pqrs updatePqrs(Pqrs pqrs) {

        PqrsEntity pqrsEntity = pqrsEntityMapper.toEntity(pqrs);
        pqrsJpaRepository.save(pqrsEntity);

        return pqrsEntityMapper.toDomain(pqrsEntity);
    }

    @Override
    public PageResponse<Pqrs> findAllById(ListPqrsByIdQuery listPqrsByIdQuery, Integer idUser) {

        Pageable pageable = PageRequest.of(
                listPqrsByIdQuery.page(),
                listPqrsByIdQuery.size(),
                Sort.by(listPqrsByIdQuery.storBy()).ascending()
        );

        Page<PqrsEntity> page = pqrsJpaRepository
                .findAllByIdUserAndDeletedAtIsNull(idUser, pageable);

        List<Pqrs> pqrs = page.getContent()
                .stream()
                .map(pqrsEntityMapper::toDomain)
                .toList();

        return new PageResponse<>(
                pqrs,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}