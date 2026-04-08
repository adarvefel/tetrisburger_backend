package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.RegisterUserCommand;

public interface RegisterUser {
    User handle(RegisterUserCommand cmd);
}
