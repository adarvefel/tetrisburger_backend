package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.SearchAdditionByName;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SearchAdditionByNameUseCase implements SearchAdditionByName {

    private final AdditionRepository repository;

    public SearchAdditionByNameUseCase(AdditionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<Addition> handle(String name, PaginationRequest pagination) {

        return repository.findByName(name.trim(), pagination);
    }
}