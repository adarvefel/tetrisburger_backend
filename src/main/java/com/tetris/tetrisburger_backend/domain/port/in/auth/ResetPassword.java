package com.tetris.tetrisburger_backend.domain.port.in.auth;

import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ResetPasswordCommand;

public interface ResetPassword {
    void handle(ResetPasswordCommand command);
}
