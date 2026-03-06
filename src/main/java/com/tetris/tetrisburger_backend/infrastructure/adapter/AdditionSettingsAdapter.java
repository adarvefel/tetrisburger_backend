package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionSettingsRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.AdditionSettingsEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.AdditionSettingsJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class AdditionSettingsAdapter implements AdditionSettingsRepository {

    private final AdditionSettingsJpaRepository jpaRepository;
    private final AdditionSettingsEntityMapper mapper;

    public AdditionSettingsAdapter(AdditionSettingsJpaRepository jpaRepository,
                                   AdditionSettingsEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AdditionSettings get() {
        return jpaRepository.findById(1)
                .map(mapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Configuración de adiciones no encontrada"
                ));
    }

    @Override
    public AdditionSettings save(AdditionSettings settings) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(settings)));
    }
}