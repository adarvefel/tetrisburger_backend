package com.tetris.tetrisburger_backend.domain.port.in.auth;

import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginUserCommand;

public interface LoginUser {
    LoginResponse execute(LoginUserCommand command);
}
