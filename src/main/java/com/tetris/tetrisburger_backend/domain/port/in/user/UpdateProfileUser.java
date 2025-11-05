package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;

public interface UpdateProfileUser {
    User handle(Integer currentId, UpdateProfileUserCommand command);  }
