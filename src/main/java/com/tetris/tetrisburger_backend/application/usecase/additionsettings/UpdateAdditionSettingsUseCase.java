package com.tetris.tetrisburger_backend.application.usecase.additionsettings;

import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;

import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.UpdateAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.command.UpdateAdditionSettingsCommand;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionSettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateAdditionSettingsUseCase implements UpdateAdditionSettings {

    private final AdditionSettingsRepository repository;

    public UpdateAdditionSettingsUseCase(AdditionSettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public AdditionSettings handle(UpdateAdditionSettingsCommand command) {
        AdditionSettings settings = repository.get();

        settings.update(
                command.maxAdditionsPerItem(),
                command.maxTotalPrice(),
                command.additionsEnabled()
        );

        return repository.save(settings);
    }
}