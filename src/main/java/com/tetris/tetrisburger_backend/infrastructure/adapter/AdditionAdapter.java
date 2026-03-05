package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.AdditionEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.AdditionJpaRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.util.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdditionAdapter implements AdditionRepository {

    private final AdditionJpaRepository jpa;
    private final AdditionEntityMapper mapper;

    public AdditionAdapter(AdditionJpaRepository jpa, AdditionEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public PageResponse<Addition> findAll(Boolean available, PaginationRequest pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize(), SortBuilder.build(pagination));

        Page<Addition> page = jpa.findAllFiltered(available, pageRequest)
                .map(mapper::toDomain);

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public Optional<Addition> findById(Integer id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpa.existsByNameIgnoreCase(name);
    }

    @Override
    public Addition save(Addition addition) {
        return mapper.toDomain(jpa.save(mapper.toEntity(addition)));
    }

    @Override
    public PageResponse<Addition> findByName(String name, PaginationRequest pagination) {
        PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize(), SortBuilder.build(pagination));

        Page<Addition> page = jpa.findByNameContaining(name, pageRequest)
                .map(mapper::toDomain);

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}