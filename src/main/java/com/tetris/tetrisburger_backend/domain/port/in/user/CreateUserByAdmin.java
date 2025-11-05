package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.CreateUserByAdminCommand;

public interface CreateUserByAdmin {
    User handle(CreateUserByAdminCommand cmd);
}
