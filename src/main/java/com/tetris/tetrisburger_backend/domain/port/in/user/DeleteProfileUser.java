package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteProfileUserCommand;

public interface DeleteProfileUser {
    void handle(DeleteProfileUserCommand command);

}