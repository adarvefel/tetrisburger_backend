package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileImageCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;

public interface UpdateProfileImage {
    User handle(Integer currentUserId,UpdateProfileImageCommand command);
}
