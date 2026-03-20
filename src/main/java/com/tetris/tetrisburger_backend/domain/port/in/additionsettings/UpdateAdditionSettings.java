package com.tetris.tetrisburger_backend.domain.port.in.additionsettings;

import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.command.UpdateAdditionSettingsCommand;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.UpdateMenuCategoryCommand;

public interface UpdateAdditionSettings {
    AdditionSettings handle(UpdateAdditionSettingsCommand command);
}
