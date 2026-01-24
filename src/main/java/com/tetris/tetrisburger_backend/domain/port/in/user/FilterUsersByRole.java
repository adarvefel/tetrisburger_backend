package com.tetris.tetrisburger_backend.domain.port.in.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.FilterUsersByRoleQuery;

public interface FilterUsersByRole {
    PageResponse<User> handle(FilterUsersByRoleQuery query);

}
