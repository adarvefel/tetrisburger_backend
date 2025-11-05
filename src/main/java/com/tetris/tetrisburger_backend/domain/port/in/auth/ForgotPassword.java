package com.tetris.tetrisburger_backend.domain.port.in.auth;

import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ForgotPasswordCommand;

public interface ForgotPassword {
    String execute(ForgotPasswordCommand command);
}
