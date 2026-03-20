package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteUserByAdminCommand;

public interface DeleteUserByAdmin {
    void handle(DeleteUserByAdminCommand command);

}
