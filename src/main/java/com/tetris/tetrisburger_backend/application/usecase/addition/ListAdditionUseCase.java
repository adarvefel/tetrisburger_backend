package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.ListAddition;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class ListAdditionUseCase implements ListAddition {

    private final AdditionRepository repository;

    public ListAdditionUseCase(AdditionRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<Addition> handle(Boolean available, PaginationRequest page) {
        return repository.findAll(available,page);
    }
}
