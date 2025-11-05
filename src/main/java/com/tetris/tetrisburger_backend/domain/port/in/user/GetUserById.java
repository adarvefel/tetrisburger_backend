package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.model.User;

public interface GetUserById {
    User handle(Integer idUser);
}
