package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.model.User;

import java.util.List;

public interface SearchUsersByEmail {
    List<User> handle(String emailPart);

}
