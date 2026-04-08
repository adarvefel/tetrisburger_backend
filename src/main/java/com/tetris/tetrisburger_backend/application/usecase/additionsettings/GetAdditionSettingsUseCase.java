package com.tetris.tetrisburger_backend.application.usecase.additionsettings;

import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.GetAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionSettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetAdditionSettingsUseCase implements GetAdditionSettings {

    private final AdditionSettingsRepository repository;

    public GetAdditionSettingsUseCase(AdditionSettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public AdditionSettings handle() {
        return repository.get();
    }
}