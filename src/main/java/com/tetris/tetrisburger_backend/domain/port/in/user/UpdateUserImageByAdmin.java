package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserImageByAdminCommand;

public interface UpdateUserImageByAdmin {
    User handle(UpdateUserImageByAdminCommand command);
}
