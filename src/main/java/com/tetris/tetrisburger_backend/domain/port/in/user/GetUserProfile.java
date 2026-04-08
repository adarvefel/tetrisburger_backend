package com.tetris.tetrisburger_backend.domain.port.in.user;


import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.GetUserProfileQuery;

public interface GetUserProfile {
    User execute(GetUserProfileQuery query);
}